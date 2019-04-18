package net.jetblack.feedbus.example.publisher;

import java.util.Scanner;

import net.jetblack.feedbus.adapters.Client;
import net.jetblack.util.Maps;

public class Program {
	
	public static void main(String[] args) {
		
		System.setProperty("net.jetblack.feedbus.adapters.SERIALIZER", "net.jetblack.feedbus.json.JsonSerializer");

		Scanner scanner = new Scanner(System.in);

		try {
			Client client = Client.createFromProperties();

			while (true) {
				System.out.println("Press p to publish q to quit");
				String line = scanner.next();
				if (line == "q") {
					break;
				}
				client.publish("LSE", "SBRY", true, Maps.mapOf("NAME", "Sainasbury PLC", "BID", 3.20, "ASK", 3.25));
			}
			client.close();
			
			scanner.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
