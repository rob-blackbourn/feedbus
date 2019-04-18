from asyncio import StreamWriter, StreamReader
from typing import Any, Iterable


class MockStreamWriter(StreamWriter):

    def __init__(self, can_write_eof: bool = True) -> None:
        self._can_write_eof = can_write_eof
        self.buf = bytes()


    def can_write_eof(self) -> bool:
        return False


    def write_eof(self) -> None:
        pass


    def transport(self) -> Any:
        return None


    def get_extra_info(self, name: str, default: Any = ...) -> Any:
        return None


    def write(self, data: bytes):
        self.buf += data


    def writelines(self, data: Iterable[bytes]):
        for line in data:
            self.buf += line


    async def drain(self) -> None:
        pass


    def close(self) -> None:
        pass


    def is_closing(self) -> bool:
        return False


class MockStreamReader(StreamReader):

    def __init__(self, buf: bytes) -> None:
        self.buf = buf
        self.index = 0


    async def read(self, n: int = -1) -> bytes:
        start = self.index

        if n == -1:
            self.index = len(self.buf)
            return self.buf[start:]

        self.index += n
        return self.buf[start: self.index]


    async def readexactly(self, n: int) -> bytes:
        return await self.read(n)


    async def readuntil(self, separator: bytes = ...) -> bytes:
        start = self.index
        while self.index < len(self.buf) and self.buf[self.index] != separator:
            self.index += 1
        return self.buf[start: self.index]


    def at_eof(self) -> bool:
        return self.index == len(self.buf)
