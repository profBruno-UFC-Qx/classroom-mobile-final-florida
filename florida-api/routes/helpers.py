from models import ItemCreate, ItemRead


def calculate_items_total(items: list[ItemCreate]) -> int:
    return sum(item.qty * item.price for item in items)


def item_to_read(item) -> ItemRead:
    return ItemRead(
        id=item.id,
        description=item.description,
        qty=item.qty,
        price=item.price,
        total=item.qty * item.price,
    )
