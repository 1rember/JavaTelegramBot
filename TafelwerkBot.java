import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.text.Document;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class TafelwerkBot extends TelegramLongPollingBot{
	List<String> phrases;
	Keys token;
	
	TafelwerkBot(){
		try {
			phrases = readFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 token = new Keys();
	}
	
	@Override
	public String getBotUsername() {
		return "tafelwerkBot";
	}
	
	@Override
	public String getBotToken() {
		return token.getToken();
	}

	@Override
	public void onUpdateReceived(Update update) {
		
		if (update.hasMessage() && update.getMessage().hasText()){
			String message_text = update.getMessage().getText();
			long chat_id = update.getMessage().getChatId();
			
			switch(message_text){
			case "/start":
				repeat(message_text, chat_id);
				break;
			case "/pic":
				pic(chat_id);
				break;
				
			case "/say":
				saySomethingNice(chat_id);
				break;
				
			case "хуй":
				checkXyu(update, chat_id);
				break;
				
			case "/get_currency":
				getBitCoin(chat_id);
				break;
				
			default:
				unknownCommand(chat_id);
				break;
			}
			
		}
		else if(update.hasMessage() && update.getMessage().hasPhoto()){
			long chat_id = update.getMessage().getChatId();
			
			List<PhotoSize> photos = update.getMessage().getPhoto();
			
			String f_id = photos.stream()
					.sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
					.findFirst()
					.orElse(null).getFileId();
			
			int f_width = photos.stream()
					.sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
					.findFirst()
					.orElse(null).getWidth();
			
			int f_height = photos.stream()
					.sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
					.findFirst()
					.orElse(null).getHeight();
			
			String caption = "file_id: " + f_id + "\nwidth: " + Integer.toString(f_width) + "\nheight" + Integer.toString(f_height);
			SendPhoto msg = new SendPhoto()
					.setChatId(chat_id)
					.setPhoto(f_id)
					.setCaption(caption);
			
			try {
				sendPhoto(msg);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
			
		}
	}

	private void unknownCommand(long chat_id) {
		SendMessage message = new SendMessage()
				.setChatId(chat_id)
				.setText("Unknown command");
		
		sMessage(message);
	}

	private void getBitCoin(long chat_id) {
		try {
			String str = getCurrency();
			repeat(str, chat_id);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkXyu(Update update, long chat_id) {
		String usr = update.getMessage().getFrom().getUserName();
		SendMessage message = new SendMessage()
				.setChatId(chat_id)
				.setText("@" + usr + ", рот твій хуй!");
		
		sMessage(message);
	}

	private void saySomethingNice(long chat_id) {
		String word = getRandomWord(phrases);
		SendMessage message = new SendMessage()
				.setChatId(chat_id)
				.setText(word);
		sMessage(message);
	}

	private void sMessage(SendMessage message) {
		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void pic(long chat_id) {
		SendPhoto msg = new SendPhoto()
				.setChatId(chat_id)
				.setPhoto("AgADAgADWagxG1F7AAFJrIh62iCb6aP2_DIOAARNSKWY56dGmxIUAQABAg")
				.setCaption("Photo");
		try {
			sendPhoto(msg);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void repeat(String message_text, long chat_id) {
		SendMessage message = new SendMessage()
				.setChatId(chat_id)
				.setText(message_text);
		sMessage(message);
	}
	
	private List<String> readFile() throws IOException{
		List<String> phrasesIn = new ArrayList<>();
		try {
			phrasesIn = Files.readAllLines(Paths.get("res\\phrases.txt"), StandardCharsets.UTF_8);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return phrasesIn;
	}
	
	private String getRandomWord(List<String> phrases){
		int randomNumber = (int)(Math.random()*phrases.size()-1);
		return phrases.get(randomNumber);
	}
	
	private String getCurrency() throws IOException{
		org.jsoup.nodes.Document doc = Jsoup.connect("https://finance.google.com/finance/converter?a=1&from=BTC&to=USD").get();
		Element currency = doc.select("span.bld").first();
		
		return currency.html();
	}

	

}
