from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, status
from sqlmodel import Session, select

from database import get_session
from models import (
    Budget,
    BudgetCreate,
    BudgetItem,
    BudgetListItem,
    BudgetRead,
    BudgetStatus,
    BudgetUpdate,
    Client,
    Receipt,
)
from routes.helpers import calculate_items_total, item_to_read


router = APIRouter(prefix="/budgets", tags=["budgets"])


def get_budget_or_404(budget_id: int, session: Session) -> Budget:
    budget = session.get(Budget, budget_id)
    if budget is None:
        raise HTTPException(status_code=404, detail="Orcamento nao encontrado")
    return budget


def validate_client(client_id: int | None, session: Session) -> None:
    if client_id is None:
        return
    if session.get(Client, client_id) is None:
        raise HTTPException(status_code=400, detail="Cliente nao encontrado")


def get_budget_items(budget_id: int, session: Session) -> list[BudgetItem]:
    return list(
        session.exec(select(BudgetItem).where(BudgetItem.budget_id == budget_id)).all()
    )


def to_budget_read(budget: Budget, session: Session) -> BudgetRead:
    items = [item_to_read(item) for item in get_budget_items(budget.id, session)]
    return BudgetRead(
        id=budget.id,
        client_id=budget.client_id,
        notes=budget.notes,
        validade=budget.validade,
        entrega=budget.entrega,
        created_at=budget.created_at,
        update_at=budget.update_at,
        total=budget.total,
        status=budget.status,
        items=items,
    )


def replace_budget_items(
    budget: Budget,
    items_data,
    session: Session,
) -> None:
    for item in get_budget_items(budget.id, session):
        session.delete(item)
    session.flush()

    for item_data in items_data:
        item = BudgetItem(
            budget_id=budget.id,
            description=item_data.description,
            qty=item_data.qty,
            price=item_data.price,
        )
        session.add(item)

    budget.total = calculate_items_total(items_data)


@router.post("/", response_model=BudgetRead, status_code=status.HTTP_201_CREATED)
def create_budget(
    budget_data: BudgetCreate,
    session: Session = Depends(get_session),
) -> BudgetRead:
    validate_client(budget_data.client_id, session)

    budget = Budget(
        client_id=budget_data.client_id,
        notes=budget_data.notes,
        validade=budget_data.validade,
        entrega=budget_data.entrega,
        total=calculate_items_total(budget_data.items)
        if budget_data.items
        else budget_data.total,
        status=budget_data.status,
    )
    session.add(budget)
    session.commit()
    session.refresh(budget)

    if budget_data.items:
        replace_budget_items(budget, budget_data.items, session)
        session.add(budget)
        session.commit()
        session.refresh(budget)

    return to_budget_read(budget, session)


@router.get("/", response_model=list[BudgetRead])
def list_budgets(session: Session = Depends(get_session)) -> list[BudgetRead]:
    budgets = session.exec(select(Budget).order_by(Budget.created_at.desc())).all()
    return [to_budget_read(budget, session) for budget in budgets]


@router.get("/list-items", response_model=list[BudgetListItem])
def list_budget_items(session: Session = Depends(get_session)) -> list[BudgetListItem]:
    budgets = session.exec(select(Budget).order_by(Budget.created_at.desc())).all()
    summaries = []

    for budget in budgets:
        client = session.get(Client, budget.client_id) if budget.client_id else None
        linked_receipt = session.exec(
            select(Receipt).where(Receipt.budget_id == budget.id)
        ).first()
        summaries.append(
            BudgetListItem(
                id=budget.id,
                client_id=budget.client_id,
                client_name=client.name if client else None,
                created_at=budget.created_at,
                total=budget.total,
                status=budget.status,
                item_count=len(get_budget_items(budget.id, session)),
                linked_receipt_id=linked_receipt.id if linked_receipt else None,
            )
        )

    return summaries


@router.get("/{budget_id}", response_model=BudgetRead)
def get_budget(
    budget_id: int,
    session: Session = Depends(get_session),
) -> BudgetRead:
    return to_budget_read(get_budget_or_404(budget_id, session), session)


@router.patch("/{budget_id}", response_model=BudgetRead)
def update_budget(
    budget_id: int,
    budget_data: BudgetUpdate,
    session: Session = Depends(get_session),
) -> BudgetRead:
    budget = get_budget_or_404(budget_id, session)
    items_data = budget_data.items if "items" in budget_data.model_fields_set else None
    data = budget_data.model_dump(exclude_unset=True)
    data.pop("items", None)

    validate_client(data.get("client_id"), session)

    for field, value in data.items():
        setattr(budget, field, value)

    if items_data is not None:
        replace_budget_items(budget, items_data, session)

    budget.update_at = datetime.now()
    session.add(budget)
    session.commit()
    session.refresh(budget)
    return to_budget_read(budget, session)


@router.patch("/{budget_id}/status", response_model=BudgetRead)
def update_budget_status(
    budget_id: int,
    budget_status: BudgetStatus,
    session: Session = Depends(get_session),
) -> BudgetRead:
    budget = get_budget_or_404(budget_id, session)
    budget.status = budget_status
    budget.update_at = datetime.now()
    session.add(budget)
    session.commit()
    session.refresh(budget)
    return to_budget_read(budget, session)


@router.delete("/{budget_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_budget(
    budget_id: int,
    session: Session = Depends(get_session),
) -> None:
    budget = get_budget_or_404(budget_id, session)
    for item in get_budget_items(budget.id, session):
        session.delete(item)
    session.delete(budget)
    session.commit()
