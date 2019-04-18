import asyncio
from feedbus import Client, JsonSerializer


async def main_async():
    client = await Client.connect('localhost', 30011, JsonSerializer())
    await client.publish('LSE', 'SBRY', True, {'name': 'Sainsbury PLC', 'BID': 1.23, 'ASK': 1.32})


if __name__ == '__main__':
    asyncio.run(main_async())
