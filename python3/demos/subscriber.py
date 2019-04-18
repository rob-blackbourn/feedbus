import asyncio
from feedbus import Client, JsonSerializer


async def main_async():
    client = await Client.connect('localhost', 30011, JsonSerializer())
    await client.add_subscription('LSE', 'SBRY')

    start_task = asyncio.create_task(client.start())
    read_task = asyncio.create_task(client.read())
    while True:
        done, pending = await asyncio.wait(
            [
                start_task,
                read_task
            ],
            return_when=asyncio.FIRST_COMPLETED
        )

        for task in done:
            if task == read_task:
                msg = read_task.result()
                read_task = asyncio.create_task(client.read())
                print(msg)
            elif task == start_task:
                print('Oh no')


if __name__ == '__main__':
    asyncio.run(main_async())
