import pytest
from feedbus.io import DataOutputStream, DataInputStream
from feedbus.messages import (
    read_message,
    write_message,
    make_multicast_data,
    make_unicast_data,
    make_forwarded_subscription_request,
    make_notification_request,
    make_subscription_request,
    make_monitor_request
)
from .mocks import MockStreamReader, MockStreamWriter


@pytest.mark.asyncio
async def test_multicast_data():
    request = make_multicast_data('LSE', 'VOD', True, b'this is not a test')
    writer = MockStreamWriter()
    await write_message(DataOutputStream(writer), request)
    reader = MockStreamReader(writer.buf)
    roundtrip = await read_message(DataInputStream(reader))
    assert request == roundtrip


@pytest.mark.asyncio
async def test_multicast_data():
    request = make_unicast_data('client', 'LSE', 'VOD', True, b'this is not a test')
    writer = MockStreamWriter()
    await write_message(DataOutputStream(writer), request)
    reader = MockStreamReader(writer.buf)
    roundtrip = await read_message(DataInputStream(reader))
    assert request == roundtrip


@pytest.mark.asyncio
async def test_forwarded_subscription_request():
    request = make_forwarded_subscription_request('client', 'LSE', 'VOD', True)
    writer = MockStreamWriter()
    await write_message(DataOutputStream(writer), request)
    reader = MockStreamReader(writer.buf)
    roundtrip = await read_message(DataInputStream(reader))
    assert request == roundtrip


@pytest.mark.asyncio
async def test_notification_request():
    request = make_notification_request('LSE', True)
    writer = MockStreamWriter()
    await write_message(DataOutputStream(writer), request)
    reader = MockStreamReader(writer.buf)
    roundtrip = await read_message(DataInputStream(reader))
    assert request == roundtrip


@pytest.mark.asyncio
async def test_subscription_request():
    request = make_subscription_request('LSE', 'VOD', True)
    writer = MockStreamWriter()
    await write_message(DataOutputStream(writer), request)
    reader = MockStreamReader(writer.buf)
    roundtrip = await read_message(DataInputStream(reader))
    assert request == roundtrip


@pytest.mark.asyncio
async def test_monitor_request():
    request = make_monitor_request('LSE', True)
    writer = MockStreamWriter()
    await write_message(DataOutputStream(writer), request)
    reader = MockStreamReader(writer.buf)
    roundtrip = await read_message(DataInputStream(reader))
    assert request == roundtrip
