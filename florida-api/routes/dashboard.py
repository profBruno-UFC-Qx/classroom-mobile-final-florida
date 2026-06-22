from datetime import datetime

from fastapi import APIRouter, Depends
from sqlmodel import Session, func, select

from database import get_session
from models import (
    Budget,
    Client,
    DashboardSummary,
    DocumentType,
    Receipt,
    RecentDocumentSummary,
)


router = APIRouter(prefix="/dashboard", tags=["dashboard"])


@router.get("/", response_model=DashboardSummary)
def get_dashboard_summary(session: Session = Depends(get_session)) -> DashboardSummary:
    now = datetime.now()
    month_start = datetime(now.year, now.month, 1)
    next_month_start = (
        datetime(now.year + 1, 1, 1)
        if now.month == 12
        else datetime(now.year, now.month + 1, 1)
    )

    client_count = session.exec(
        select(func.count(Client.id)).where(Client.deleted == False)
    ).one()
    budget_count = session.exec(select(func.count(Budget.id))).one()
    receipt_count = session.exec(select(func.count(Receipt.id))).one()
    total_budgeted = session.exec(select(func.coalesce(func.sum(Budget.total), 0))).one()
    total_received = session.exec(select(func.coalesce(func.sum(Receipt.total), 0))).one()
    month_received = session.exec(
        select(func.coalesce(func.sum(Receipt.total), 0)).where(
            Receipt.date >= month_start,
            Receipt.date < next_month_start,
        )
    ).one()

    budgets = session.exec(select(Budget)).all()
    receipts = session.exec(select(Receipt)).all()
    recent_documents = [
        RecentDocumentSummary(
            type=DocumentType.BUDGET,
            document_id=budget.id,
            client_name=(session.get(Client, budget.client_id).name if budget.client_id and session.get(Client, budget.client_id) else ""),
            total=budget.total,
            created_at=budget.created_at,
        )
        for budget in budgets
    ]
    recent_documents.extend(
        RecentDocumentSummary(
            type=DocumentType.RECEIPT,
            document_id=receipt.id,
            client_name=(session.get(Client, receipt.client_id).name if receipt.client_id and session.get(Client, receipt.client_id) else ""),
            total=receipt.total,
            created_at=receipt.date,
        )
        for receipt in receipts
    )
    recent_documents = sorted(
        recent_documents,
        key=lambda document: document.created_at,
        reverse=True,
    )[:7]

    return DashboardSummary(
        client_count=client_count,
        budget_count=budget_count,
        receipt_count=receipt_count,
        total_budgeted=total_budgeted,
        total_received=total_received,
        month_received=month_received,
        recent_documents=recent_documents,
    )
