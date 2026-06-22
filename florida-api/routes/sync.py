from fastapi import APIRouter, Depends
from sqlmodel import Session, select

from database import get_session
from models import Budget, Client, Receipt, SyncPayload, UserSetup
from routes.budgets import to_budget_read
from routes.receipts import to_receipt_read


router = APIRouter(prefix="/sync", tags=["sync"])


@router.get("/", response_model=SyncPayload)
def get_sync_payload(session: Session = Depends(get_session)) -> SyncPayload:
    user_setup = session.exec(select(UserSetup)).first()
    clients = session.exec(select(Client).order_by(Client.name)).all()
    budgets = session.exec(select(Budget).order_by(Budget.created_at.desc())).all()
    receipts = session.exec(select(Receipt).order_by(Receipt.date.desc())).all()

    return SyncPayload(
        user_setup=user_setup,
        clients=list(clients),
        budgets=[to_budget_read(budget, session) for budget in budgets],
        receipts=[to_receipt_read(receipt, session) for receipt in receipts],
    )
