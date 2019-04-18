from __future__ import annotations
import asyncio
from asyncio import StreamReader, StreamWriter
import logging
from typing import Mapping, Any
from .io import DataInputStream, DataOutputStream
from .types import ByteSerializable
from .messages import (
    MessageType,
    read_message,
    write_message,
    make_multicast_data,
    make_unicast_data,
    make_subscription_request,
    make_notification_request,
    make_monitor_request
)

log = logging.getLogger(__name__)

MULTICAST_DATA = 0
UNICAST_DATA = 1
FORWARDED_SUBSCRIPTION_REQUEST = 2
NOTIFICATION_REQUEST = 3
SUBSCRIPTION_REQUEST = 4
MONITOR_REQUEST = 5


class Client:

    def __init__(self, reader: StreamReader, writer: StreamWriter, serializer: ByteSerializable):
        self._input_stream = DataInputStream(reader)
        self._output_stream = DataOutputStream(writer)
        self._serializer = serializer
        self._token = asyncio.Event()
        self._queue: asyncio.Queue[Mapping[str, Any]] = asyncio.Queue()


    @classmethod
    async def connect(cls, host: str, port: int, serializer: ByteSerializable) -> Client:
        reader, writer = await asyncio.open_connection(host, port)
        return Client(reader, writer, serializer)


    async def start(self):

        token_wait_task = asyncio.create_task(self._token.wait())
        read_message_task = asyncio.create_task(read_message(self._input_stream))

        while not self._token.is_set():

            log.debug('Waiting for event')
            done, pending = await asyncio.wait([
                token_wait_task,
                read_message_task
            ], return_when=asyncio.FIRST_COMPLETED)

            for task in done:
                if task == read_message_task:
                    msg = read_message_task.result()
                    read_message_task = asyncio.create_task(read_message(self._input_stream))
                    if msg['type'] in (MessageType.unicast_data.name, MessageType.multicast_data.name):
                        msg['data'] = self._serializer.deserialize(msg['data'])
                    await self._queue.put(msg)
                elif task == token_wait_task:
                    log.debug('Exiting loop')

        log.info('Done')


    async def stop(self):
        self._token.set()


    async def read(self) -> Mapping[str, Any]:
        msg = await self._queue.get()
        return msg


    async def publish(self, feed: str, topic: str, is_image: bool, data: Any) -> None:
        buf = self._serializer.serialize(data)
        msg = make_multicast_data(feed, topic, is_image, buf)
        await write_message(self._output_stream, msg)


    async def send(self, client_id: str, feed: str, topic: str, is_image: bool, data: Any) -> None:
        buf = self._serializer.serialize(data)
        msg = make_unicast_data(client_id, feed, topic, is_image, buf)
        await write_message(self._output_stream, msg)


    async def add_subscription(self, feed: str, topic: str) -> None:
        msg = make_subscription_request(feed, topic, True)
        await write_message(self._output_stream, msg)


    async def remove_subscription(self, feed: str, topic: str) -> None:
        msg = make_subscription_request(feed, topic, False)
        await write_message(self._output_stream, msg)


    async def add_notification(self, feed: str) -> None:
        msg = make_notification_request(feed, True)
        await write_message(self._output_stream, msg)


    async def remove_notification(self, feed: str) -> None:
        msg = make_notification_request(feed, False)
        await write_message(self._output_stream, msg)


    async def add_monitor(self, feed: str) -> None:
        msg = make_monitor_request(feed, True)
        await write_message(self._output_stream, msg)


    async def remove_monitor(self, feed: str) -> None:
        msg = make_monitor_request(feed, False)
        await write_message(self._output_stream, msg)
