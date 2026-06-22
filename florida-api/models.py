from datetime import datetime
from enum import Enum

from pydantic import ConfigDict
from sqlmodel import Field, SQLModel


def to_camel(value: str) -> str:
    parts = value.split("_")
    return parts[0] + "".join(part.capitalize() for part in parts[1:])


class ApiModel(SQLModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)


class BudgetStatus(str, Enum):
    DRAFT = "DRAFT"
    SENT = "SENT"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
    EXPIRED = "EXPIRED"


class DocumentType(str, Enum):
    BUDGET = "BUDGET"
    RECEIPT = "RECEIPT"


class ClientBase(ApiModel):
    name: str
    address: str
    document: str
    phone: str
    image_path: str | None = None
    deleted: bool = False


class Client(ClientBase, table=True):
    id: int | None = Field(default=None, primary_key=True)


class ClientCreate(ClientBase):
    pass


class ClientUpdate(ApiModel):
    name: str | None = None
    address: str | None = None
    document: str | None = None
    phone: str | None = None
    image_path: str | None = None
    deleted: bool | None = None


class ClientListItem(ApiModel):
    id: int
    name: str
    address: str
    document: str
    phone: str
    image_path: str | None = None


class ClientDocumentSummary(ApiModel):
    type: DocumentType
    document_id: int
    date: datetime
    total: int


class UserSetupBase(ApiModel):
    name: str = "Francisco"
    document: str = "06364254307"
    street: str = "Rua dos Bobos"
    number: str = "0"
    neighborhood: str = "Bairro dos Bobos"
    city: str = "Cidade dos Bobos"
    state: str = "SP"
    phone: str = "11999999999"
    image_path: str | None = None


class UserSetup(UserSetupBase, table=True):
    id: int = Field(default=1, primary_key=True)


class UserSetupCreate(UserSetupBase):
    pass


class UserSetupUpdate(ApiModel):
    name: str | None = None
    document: str | None = None
    street: str | None = None
    number: str | None = None
    neighborhood: str | None = None
    city: str | None = None
    state: str | None = None
    phone: str | None = None
    image_path: str | None = None


class ItemBase(ApiModel):
    description: str
    qty: int
    price: int


class ItemCreate(ItemBase):
    pass


class ItemRead(ItemBase):
    id: int
    total: int


class BudgetBase(ApiModel):
    client_id: int | None = Field(default=None, foreign_key="client.id")
    notes: str | None = None
    validade: str | None = None
    entrega: str | None = None
    total: int = 0
    status: BudgetStatus = BudgetStatus.DRAFT


class Budget(BudgetBase, table=True):
    id: int | None = Field(default=None, primary_key=True)
    created_at: datetime = Field(default_factory=datetime.now)
    update_at: datetime = Field(default_factory=datetime.now)


class BudgetItem(ItemBase, table=True):
    id: int | None = Field(default=None, primary_key=True)
    budget_id: int = Field(foreign_key="budget.id")


class BudgetCreate(BudgetBase):
    items: list[ItemCreate] = []


class BudgetUpdate(ApiModel):
    client_id: int | None = None
    notes: str | None = None
    validade: str | None = None
    entrega: str | None = None
    total: int | None = None
    status: BudgetStatus | None = None
    items: list[ItemCreate] | None = None


class BudgetRead(BudgetBase):
    id: int
    created_at: datetime
    update_at: datetime
    items: list[ItemRead] = []


class BudgetListItem(ApiModel):
    id: int
    client_id: int | None = None
    client_name: str | None = None
    created_at: datetime
    total: int
    status: BudgetStatus
    item_count: int
    linked_receipt_id: int | None = None


class ReceiptBase(ApiModel):
    client_id: int | None = Field(default=None, foreign_key="client.id")
    budget_id: int | None = Field(default=None, foreign_key="budget.id")
    total: int = 0
    date: datetime = Field(default_factory=datetime.now)


class Receipt(ReceiptBase, table=True):
    id: int | None = Field(default=None, primary_key=True)
    created_at: datetime = Field(default_factory=datetime.now)


class ReceiptItem(ItemBase, table=True):
    id: int | None = Field(default=None, primary_key=True)
    receipt_id: int = Field(foreign_key="receipt.id")


class ReceiptCreate(ReceiptBase):
    items: list[ItemCreate] = []


class ReceiptUpdate(ApiModel):
    client_id: int | None = None
    budget_id: int | None = None
    total: int | None = None
    date: datetime | None = None
    items: list[ItemCreate] | None = None


class ReceiptRead(ReceiptBase):
    id: int
    created_at: datetime
    items: list[ItemRead] = []


class ReceiptListItem(ApiModel):
    id: int
    client_id: int | None = None
    client_name: str | None = None
    budget_id: int | None = None
    total: int
    date: datetime
    item_count: int


class RecentDocumentSummary(ApiModel):
    type: DocumentType
    document_id: int
    client_name: str
    total: int
    created_at: datetime


class DashboardSummary(ApiModel):
    client_count: int = 0
    budget_count: int = 0
    receipt_count: int = 0
    total_budgeted: int = 0
    total_received: int = 0
    month_received: int = 0
    recent_documents: list[RecentDocumentSummary] = []


class SyncPayload(ApiModel):
    user_setup: UserSetup | None = None
    clients: list[Client] = []
    budgets: list[BudgetRead] = []
    receipts: list[ReceiptRead] = []
