from asyncio import StreamReader, StreamWriter
from enum import Enum
from typing import Mapping, Any
from .io import DataInputStream, DataOutputStream


class MessageType(Enum):
    multicast_data = 0
    unicast_data = 1
    forwarded_subscription_request = 2
    notification_request = 3
    subscription_request = 4
    monitor_request = 5


def make_multicast_data(feed: str, topic: str, is_image: bool, data: bytes) -> Mapping[str, Any]:
    return {
        'type': MessageType.multicast_data.name,
        'feed': feed,
        'topic': topic,
        'is_image': is_image,
        'data': data
    }


def make_unicast_data(client_id: str, feed: str, topic: str, is_image: bool, data: bytes) -> Mapping[str, Any]:
    return {
        'type': MessageType.unicast_data.name,
        'client_id': client_id,
        'feed': feed,
        'topic': topic,
        'is_image': is_image,
        'data': data
    }


def make_forwarded_subscription_request(client_id: str, feed: str, topic: str, is_add: bool) -> Mapping[str, Any]:
    return {
        'type': MessageType.forwarded_subscription_request.name,
        'client_id': client_id,
        'feed': feed,
        'topic': topic,
        'is_add': is_add
    }


def make_notification_request(feed: str, is_add: bool) -> Mapping[str, Any]:
    return {
        'type': MessageType.notification_request.name,
        'feed': feed,
        'is_add': is_add
    }


def make_subscription_request(feed: str, topic: str, is_add: bool) -> Mapping[str, Any]:
    return {
        'type': MessageType.subscription_request.name,
        'feed': feed,
        'topic': topic,
        'is_add': is_add
    }


def make_monitor_request(feed: str, is_add: bool) -> Mapping[str, Any]:
    return {
        'type': MessageType.monitor_request.name,
        'feed': feed,
        'is_add': is_add
    }


async def read_message(stream: DataInputStream) -> Mapping[str, Any]:
    msg_type = MessageType(await stream.read_byte())

    if msg_type == MessageType.multicast_data:
        feed = await stream.read_utf()
        topic = await stream.read_utf()
        is_image = await stream.read_boolean()
        data_len = await stream.read_int()
        data = await stream.read_fully(data_len)
        return make_multicast_data(feed, topic, is_image, data)
    elif msg_type == MessageType.unicast_data:
        client_id = await stream.read_utf()
        feed = await stream.read_utf()
        topic = await stream.read_utf()
        is_image = await stream.read_boolean()
        data_len = await stream.read_int()
        data = await stream.read_fully(data_len)
        return make_unicast_data(client_id, feed, topic, is_image, data)
    elif msg_type == MessageType.forwarded_subscription_request:
        client_id = await stream.read_utf()
        feed = await stream.read_utf()
        topic = await stream.read_utf()
        is_add = await stream.read_boolean()
        return make_forwarded_subscription_request(client_id, feed, topic, is_add)
    elif msg_type == MessageType.notification_request:
        feed = await stream.read_utf()
        is_add = await stream.read_boolean()
        return make_notification_request(feed, is_add)
    elif msg_type == MessageType.subscription_request:
        feed = await stream.read_utf()
        topic = await stream.read_utf()
        is_add = await stream.read_boolean()
        return make_subscription_request(feed, topic, is_add)
    elif msg_type == MessageType.monitor_request:
        feed = await stream.read_utf()
        is_add = await stream.read_boolean()
        return make_monitor_request(feed, is_add)
    else:
        raise RuntimeError(f'Unhandleed message type {msg_type}')


async def write_message(stream: DataOutputStream, msg: Mapping[str, Any]) -> None:
    msg_type = MessageType[msg['type']]
    stream.write_byte(msg_type.value)

    if msg_type == MessageType.multicast_data:
        stream.write_utf(msg['feed'])
        stream.write_utf(msg['topic'])
        stream.write_boolean(msg['is_image'])
        data = msg['data']
        stream.write_int(len(data))
        stream.write_fully(data)
        await stream.drain()
    elif msg_type == MessageType.unicast_data:
        stream.write_utf(msg['client_id'])
        stream.write_utf(msg['feed'])
        stream.write_utf(msg['topic'])
        stream.write_boolean(msg['is_image'])
        data = msg['data']
        stream.write_int(len(data))
        stream.write_fully(data)
        await stream.drain()
    elif msg_type == MessageType.forwarded_subscription_request:
        stream.write_utf(msg['client_id'])
        stream.write_utf(msg['feed'])
        stream.write_utf(msg['topic'])
        stream.write_boolean(msg['is_add'])
        await stream.drain()
    elif msg_type == MessageType.notification_request:
        stream.write_utf(msg['feed'])
        stream.write_boolean(msg['is_add'])
        await stream.drain()
    elif msg_type == MessageType.subscription_request:
        stream.write_utf(msg['feed'])
        stream.write_utf(msg['topic'])
        stream.write_boolean(msg['is_add'])
        await stream.drain()
    elif msg_type == MessageType.monitor_request:
        stream.write_utf(msg['feed'])
        stream.write_boolean(msg['is_add'])
        await stream.drain()
    else:
        raise RuntimeError(f'Unhandleed message type {msg_type}')
