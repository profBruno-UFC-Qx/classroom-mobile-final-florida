from fastapi import APIRouter, Depends
from sqlmodel import Session, select

from database import get_session
from models import UserSetup, UserSetupCreate, UserSetupUpdate


router = APIRouter(prefix="/user-setup", tags=["user setup"])


@router.get("/", response_model=UserSetup)
def get_user_setup(session: Session = Depends(get_session)) -> UserSetup:
    setup = session.exec(select(UserSetup)).first()
    if setup is not None:
        return setup

    setup = UserSetup()
    session.add(setup)
    session.commit()
    session.refresh(setup)
    return setup


@router.put("/", response_model=UserSetup)
def save_user_setup(
    setup_data: UserSetupCreate,
    session: Session = Depends(get_session),
) -> UserSetup:
    setup = session.exec(select(UserSetup)).first()
    if setup is None:
        setup = UserSetup.model_validate(setup_data)
    else:
        for field, value in setup_data.model_dump().items():
            setattr(setup, field, value)

    session.add(setup)
    session.commit()
    session.refresh(setup)
    return setup


@router.patch("/", response_model=UserSetup)
def update_user_setup(
    setup_data: UserSetupUpdate,
    session: Session = Depends(get_session),
) -> UserSetup:
    setup = session.exec(select(UserSetup)).first()
    if setup is None:
        setup = UserSetup()

    for field, value in setup_data.model_dump(exclude_unset=True).items():
        setattr(setup, field, value)

    session.add(setup)
    session.commit()
    session.refresh(setup)
    return setup
