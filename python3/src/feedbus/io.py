from asyncio import StreamReader, StreamWriter
import struct


class DataInputStream:

    def __init__(self, stream: StreamReader):
        self.stream = stream


    async def read_boolean(self) -> bool:
        buf = await self.stream.readexactly(1)
        return struct.unpack('?', buf)[0]


    async def read_byte(self) -> int:
        buf = await self.stream.readexactly(1)
        return struct.unpack('b', buf)[0]


    async def read_unsigned_byte(self) -> int:
        buf = await self.stream.readexactly(1)
        return struct.unpack('B', buf)[0]


    async def read_short(self) -> int:
        buf = await self.stream.readexactly(2)
        return struct.unpack('>h', buf)[0]


    async def read_unsigned_short(self) -> int:
        buf = await self.stream.readexactly(2)
        return struct.unpack('>H', buf)[0]


    async def read_int(self) -> int:
        buf = await self.stream.readexactly(4)
        return struct.unpack('>i', buf)[0]


    async def read_long(self) -> int:
        buf = await self.stream.readexactly(8)
        return struct.unpack('>q', buf)[0]


    async def read_char(self) -> str:
        buf = await self.stream.readexactly(2)
        return chr(struct.unpack('>H', buf)[0])


    async def read_float(self) -> float:
        buf = await self.stream.readexactly(4)
        return struct.unpack('>f', buf)[0]


    async def read_double(self) -> float:
        buf = await self.stream.readexactly(8)
        return struct.unpack('>d', buf)[0]


    async def read_utf(self) -> str:
        buf = await self.stream.readexactly(2)
        utf_len = struct.unpack('>H', buf)[0]
        buf = await self.stream.readexactly(utf_len)
        return buf.decode('utf-8')


    async def read_fully(self, n: int) -> bytes:
        buf = await self.stream.readexactly(n)
        return buf


class DataOutputStream:

    def __init__(self, stream: StreamWriter) -> None:
        self.stream = stream


    def write_boolean(self, val: bool) -> None:
        buf = struct.pack('?', val)
        self.stream.write(buf)


    def write_byte(self, val: int) -> None:
        buf = struct.pack('b', val)
        self.stream.write(buf)


    def write_unsigned_byte(self, val: int) -> None:
        buf = struct.pack('B', val)
        self.stream.write(buf)


    def write_short(self, val: int) -> None:
        buf = struct.pack('>h', val)
        self.stream.write(buf)


    def write_unsigned_short(self, val: int) -> None:
        buf = struct.pack('>H', val)
        self.stream.write(buf)


    def write_int(self, val) -> None:
        self.stream.write(struct.pack('>i', val))


    def write_long(self, val: int) -> None:
        buf = struct.pack('>q', val)
        self.stream.write(buf)


    def write_float(self, val: float) -> None:
        buf = struct.pack('>f', val)
        self.stream.write(buf)


    def write_double(self, val: int) -> None:
        buf = struct.pack('>d', val)
        self.stream.write(buf)


    def write_char(self, val: str) -> None:
        buf = struct.pack('>H', ord(val[0]))
        self.stream.write(buf)


    def write_utf(self, val: str) -> None:
        buf = struct.pack('>H', len(val))
        self.stream.write(buf)
        buf = val.encode('utf-8')
        self.stream.write(buf)


    def write_fully(self, val: bytes) -> None:
        self.stream.write(val)


    async def drain(self) -> None:
        await self.stream.drain()
