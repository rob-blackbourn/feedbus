from abc import ABCMeta, abstractmethod
from typing import Any


class ByteSerializable(metaclass=ABCMeta):

    @abstractmethod
    def deserialize(self, buf: bytes) -> Any:
        ...


    @abstractmethod
    def serialize(self, obj: Any) -> bytes:
        ...

