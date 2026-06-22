from collections.abc import Generator
from os import getenv

from sqlmodel import Session, SQLModel, create_engine

DATABASE_URL = getenv("FLORIDA_DATABASE_URL", "sqlite:///florida.db")

engine = create_engine(
    DATABASE_URL,
    connect_args={"check_same_thread": False},
)


def create_florida_db() -> None:
    SQLModel.metadata.create_all(engine)


def get_session() -> Generator[Session]:
    with Session(engine) as session:
        yield session
