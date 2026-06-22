from contextlib import asynccontextmanager

from fastapi import FastAPI

from database import create_florida_db
from routes import budgets, clients, dashboard, receipts, sync, user_setup


@asynccontextmanager
async def lifespan(app: FastAPI):
    create_florida_db()
    yield


app = FastAPI(
    title="Florida API",
    version="0.1.0",
    lifespan=lifespan,
)


@app.get("/")
def read_root():
    return {"message": "Florida API"}


app.include_router(clients.router)
app.include_router(budgets.router)
app.include_router(receipts.router)
app.include_router(user_setup.router)
app.include_router(dashboard.router)
app.include_router(sync.router)
