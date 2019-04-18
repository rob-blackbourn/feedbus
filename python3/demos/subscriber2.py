import aioconsole
import asyncio
from feedbus import Client, JsonSerializer


async def main_async():
    client = await Client.connect('localhost', 30011, JsonSerializer())
    start_task = asyncio.create_task(client.start())
    read_task = asyncio.create_task(client.read())
    console_task = asyncio.create_task(aioconsole.ainput('Enter +/- feed and topic (e.g. + LSE SBRY)\n'))

    while True:

        done, pending = await asyncio.wait(
            [start_task, read_task, console_task],
            return_when=asyncio.FIRST_COMPLETED
        )

        for task in done:
            if task == console_task:
                line = console_task.result()
                if line:
                    try:
                        add_remove, feed, topic = line.split(' ')
                        print(
                            f'{"Subscribing" if add_remove == "+" else "Unsubscribing"} to feed \"{feed}\" and topic \"{topic}\"')
                        if add_remove == '+':
                            await client.add_subscription(feed, topic)
                        else:
                            await client.remove_subscription(feed, topic)
                    except:
                        print(f'Invalid line: \"{line}\"')
                console_task = asyncio.create_task(aioconsole.ainput('Enter +/- feed and topic (e.g. LSE SBRY)\n'))
            elif task == read_task:
                msg = read_task.result()
                print(msg)
                read_task = asyncio.create_task(client.read())


if __name__ == '__main__':
    asyncio.run(main_async())
