from contextlib import asynccontextmanager

from fastapi import FastAPI

from database import create_florida_db


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
