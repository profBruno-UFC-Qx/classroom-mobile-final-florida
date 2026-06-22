from fastapi import APIRouter, Depends, HTTPException, status
from sqlmodel import Session, select

from database import get_session
from models import (
    Client,
    ClientCreate,
    ClientDocumentSummary,
    ClientListItem,
    ClientUpdate,
    DocumentType,
)


router = APIRouter(prefix="/clients", tags=["clients"])


@router.post("/", response_model=Client, status_code=status.HTTP_201_CREATED)
def create_client(
    client_data: ClientCreate,
    session: Session = Depends(get_session),
) -> Client:
    client = Client.model_validate(client_data)
    session.add(client)
    session.commit()
    session.refresh(client)
    return client


@router.get("/", response_model=list[Client])
def list_clients(session: Session = Depends(get_session)) -> list[Client]:
    statement = select(Client).where(Client.deleted == False).order_by(Client.name)
    return list(session.exec(statement).all())


@router.get("/list-items", response_model=list[ClientListItem])
def list_client_items(session: Session = Depends(get_session)) -> list[ClientListItem]:
    clients = session.exec(
        select(Client).where(Client.deleted == False).order_by(Client.name)
    ).all()
    return [
        ClientListItem(
            id=client.id,
            name=client.name,
            address=client.address,
            document=client.document,
            phone=client.phone,
            image_path=client.image_path,
        )
        for client in clients
    ]


@router.get("/{client_id}", response_model=Client)
def get_client(
    client_id: int,
    session: Session = Depends(get_session),
) -> Client:
    client = session.get(Client, client_id)
    if client is None:
        raise HTTPException(status_code=404, detail="Cliente nao encontrado")
    return client


@router.get("/{client_id}/documents", response_model=list[ClientDocumentSummary])
def list_client_documents(
    client_id: int,
    session: Session = Depends(get_session),
) -> list[ClientDocumentSummary]:
    if session.get(Client, client_id) is None:
        raise HTTPException(status_code=404, detail="Cliente nao encontrado")

    from models import Budget, Receipt

    budgets = session.exec(select(Budget).where(Budget.client_id == client_id)).all()
    receipts = session.exec(select(Receipt).where(Receipt.client_id == client_id)).all()
    documents = [
        ClientDocumentSummary(
            type=DocumentType.BUDGET,
            document_id=budget.id,
            date=budget.created_at,
            total=budget.total,
        )
        for budget in budgets
    ]
    documents.extend(
        ClientDocumentSummary(
            type=DocumentType.RECEIPT,
            document_id=receipt.id,
            date=receipt.date,
            total=receipt.total,
        )
        for receipt in receipts
    )
    return sorted(documents, key=lambda document: document.date, reverse=True)


@router.patch("/{client_id}", response_model=Client)
def update_client(
    client_id: int,
    client_data: ClientUpdate,
    session: Session = Depends(get_session),
) -> Client:
    client = session.get(Client, client_id)
    if client is None:
        raise HTTPException(status_code=404, detail="Cliente nao encontrado")

    for field, value in client_data.model_dump(exclude_unset=True).items():
        setattr(client, field, value)

    session.add(client)
    session.commit()
    session.refresh(client)
    return client


@router.delete("/{client_id}", response_model=Client)
def delete_client(
    client_id: int,
    session: Session = Depends(get_session),
) -> Client:
    client = session.get(Client, client_id)
    if client is None:
        raise HTTPException(status_code=404, detail="Cliente nao encontrado")

    client.deleted = True
    session.add(client)
    session.commit()
    session.refresh(client)
    return client
