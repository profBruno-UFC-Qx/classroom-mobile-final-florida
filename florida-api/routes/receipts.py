from fastapi import APIRouter, Depends, HTTPException, status
from sqlmodel import Session, select

from database import get_session
from models import (
    Budget,
    Client,
    Receipt,
    ReceiptCreate,
    ReceiptItem,
    ReceiptListItem,
    ReceiptRead,
    ReceiptUpdate,
)
from routes.helpers import calculate_items_total, item_to_read


router = APIRouter(prefix="/receipts", tags=["receipts"])


def get_receipt_or_404(receipt_id: int, session: Session) -> Receipt:
    receipt = session.get(Receipt, receipt_id)
    if receipt is None:
        raise HTTPException(status_code=404, detail="Recibo nao encontrado")
    return receipt


def validate_receipt_relations(
    client_id: int | None,
    budget_id: int | None,
    session: Session,
) -> None:
    if client_id is not None and session.get(Client, client_id) is None:
        raise HTTPException(status_code=400, detail="Cliente nao encontrado")
    if budget_id is not None and session.get(Budget, budget_id) is None:
        raise HTTPException(status_code=400, detail="Orcamento nao encontrado")


def get_receipt_items(receipt_id: int, session: Session) -> list[ReceiptItem]:
    return list(
        session.exec(
            select(ReceiptItem).where(ReceiptItem.receipt_id == receipt_id)
        ).all()
    )


def to_receipt_read(receipt: Receipt, session: Session) -> ReceiptRead:
    items = [item_to_read(item) for item in get_receipt_items(receipt.id, session)]
    return ReceiptRead(
        id=receipt.id,
        client_id=receipt.client_id,
        budget_id=receipt.budget_id,
        total=receipt.total,
        date=receipt.date,
        created_at=receipt.created_at,
        items=items,
    )


def replace_receipt_items(
    receipt: Receipt,
    items_data,
    session: Session,
) -> None:
    for item in get_receipt_items(receipt.id, session):
        session.delete(item)
    session.flush()

    for item_data in items_data:
        item = ReceiptItem(
            receipt_id=receipt.id,
            description=item_data.description,
            qty=item_data.qty,
            price=item_data.price,
        )
        session.add(item)

    receipt.total = calculate_items_total(items_data)


@router.post("/", response_model=ReceiptRead, status_code=status.HTTP_201_CREATED)
def create_receipt(
    receipt_data: ReceiptCreate,
    session: Session = Depends(get_session),
) -> ReceiptRead:
    validate_receipt_relations(receipt_data.client_id, receipt_data.budget_id, session)

    if receipt_data.budget_id is not None:
        existing = session.exec(
            select(Receipt).where(Receipt.budget_id == receipt_data.budget_id)
        ).first()
        if existing is not None:
            return to_receipt_read(existing, session)

    receipt = Receipt(
        client_id=receipt_data.client_id,
        budget_id=receipt_data.budget_id,
        total=calculate_items_total(receipt_data.items)
        if receipt_data.items
        else receipt_data.total,
        date=receipt_data.date,
    )
    session.add(receipt)
    session.commit()
    session.refresh(receipt)

    if receipt_data.items:
        replace_receipt_items(receipt, receipt_data.items, session)
        session.add(receipt)
        session.commit()
        session.refresh(receipt)

    return to_receipt_read(receipt, session)


@router.get("/", response_model=list[ReceiptRead])
def list_receipts(session: Session = Depends(get_session)) -> list[ReceiptRead]:
    receipts = session.exec(select(Receipt).order_by(Receipt.date.desc())).all()
    return [to_receipt_read(receipt, session) for receipt in receipts]


@router.get("/list-items", response_model=list[ReceiptListItem])
def list_receipt_items(session: Session = Depends(get_session)) -> list[ReceiptListItem]:
    receipts = session.exec(select(Receipt).order_by(Receipt.date.desc())).all()
    summaries = []

    for receipt in receipts:
        client = session.get(Client, receipt.client_id) if receipt.client_id else None
        summaries.append(
            ReceiptListItem(
                id=receipt.id,
                client_id=receipt.client_id,
                client_name=client.name if client else None,
                budget_id=receipt.budget_id,
                total=receipt.total,
                date=receipt.date,
                item_count=len(get_receipt_items(receipt.id, session)),
            )
        )

    return summaries


@router.get("/{receipt_id}", response_model=ReceiptRead)
def get_receipt(
    receipt_id: int,
    session: Session = Depends(get_session),
) -> ReceiptRead:
    return to_receipt_read(get_receipt_or_404(receipt_id, session), session)


@router.patch("/{receipt_id}", response_model=ReceiptRead)
def update_receipt(
    receipt_id: int,
    receipt_data: ReceiptUpdate,
    session: Session = Depends(get_session),
) -> ReceiptRead:
    receipt = get_receipt_or_404(receipt_id, session)
    items_data = receipt_data.items if "items" in receipt_data.model_fields_set else None
    data = receipt_data.model_dump(exclude_unset=True)
    data.pop("items", None)

    validate_receipt_relations(data.get("client_id"), data.get("budget_id"), session)

    for field, value in data.items():
        setattr(receipt, field, value)

    if items_data is not None:
        replace_receipt_items(receipt, items_data, session)

    session.add(receipt)
    session.commit()
    session.refresh(receipt)
    return to_receipt_read(receipt, session)


@router.delete("/{receipt_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_receipt(
    receipt_id: int,
    session: Session = Depends(get_session),
) -> None:
    receipt = get_receipt_or_404(receipt_id, session)
    for item in get_receipt_items(receipt.id, session):
        session.delete(item)
    session.delete(receipt)
    session.commit()
