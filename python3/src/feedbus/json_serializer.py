import json
from typing import Mapping, List, Any, Optional, Union, Callable
from .types import ByteSerializable

JsonObject = Union[List[Any], Mapping[str, Any]]


class JsonSerializer(ByteSerializable):

    def __init__(
            self,
            loads: Optional[Callable[[str], JsonObject]] = None,
            dumps: Optional[Callable[[JsonObject], str]] = None
    ) -> None:
        self._loads = loads or json.loads
        self._dumps = dumps or json.dumps


    def deserialize(self, buf: bytes) -> JsonObject:
        text = buf.decode('utf-8')
        return self._loads(text)


    def serialize(self, obj: JsonObject) -> bytes:
        text = self._dumps(obj)
        return text.encode('utf-8')
