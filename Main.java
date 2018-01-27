import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main {

	public static void main(String[] args) {
		//Initialize Api Context
		ApiContextInitializer.init();
		//Instantiate Telegram Bots API
		TelegramBotsApi botsApi = new TelegramBotsApi();
		
		try {
			botsApi.registerBot(new TafelwerkBot());
			System.out.println("Bot is running");
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

}
