package apiTestCapstone;

import java.lang.module.ModuleDescriptor.Builder;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;


public class ApiTests {

	private final String USERNAME = "test";
	private final String PASSWORD = "test";
	private String fNameString;
	private String lNameString;
	private String EMAIL;

	private boolean DEBUG = false;

	private String jWTtokenString;
	private int userId;
	
	private String tranDesc;
	private String tranAmount;
	private String tranCat;
	private LocalDate tranDate;
	private String tranId;
	
	private String catId;
	private Double maxSpendAmount;
	
	private String baseURLString = "";
	
	public void createUser() {
		System.out.printf("Test Case 1.1 (create user) ");
		Random rand = new Random();
		fNameString = String.valueOf(rand.nextInt(10000));
		lNameString = String.valueOf(rand.nextInt(10000));
		
		JSONObject json = new JSONObject();
		json.put("username", USERNAME);
		json.put("password", PASSWORD);
		json.put("email", EMAIL);
		json.put("firstName", fNameString);
		json.put("lastName", lNameString);

		String jsonData = json.toString();
		//String jsonData = "{ \"username\": \"" + USERNAME + "\", \"password\": \"" + PASSWORD + "\" , \"email\": \""
		//		+ EMAIL + "\"}";

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://bapi.chirpich.org/api/auth/register"))
				.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonData)).build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			if (DEBUG) {
				System.out.printf("DEBUG: %s: %s", response.statusCode(), response.body());
			}
			if (response.body().equals("User registered successfully!")) {
				System.out.printf(" PASSED ");
			} else {
				System.out.printf(" FAILED %s", response.body());
			}
			// System.out.println(response.body());
		} catch (Exception e) {
			System.out.printf(" FAILED ");
			System.out.printf(" Error: %s" + e);
		}
		System.out.printf("\n");
	}

	public void loginToUser() {
		System.out.printf("Test Case 1.2 (login user) ");
		//String jsonData = "{ \"email\": \"" + EMAIL + "\", \"password\": \"" + PASSWORD + "\" }";
		JSONObject json = new JSONObject();
		json.put("email", EMAIL);  // or just userId if it's a String or UUID
		json.put("password", PASSWORD);
		json.put("rememberMe", false);


		String jsonData = json.toString();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURLString + "/api/auth/login"))
				.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonData)).build();
		HttpResponse<String> response;
		try {
			response = client.send(request, BodyHandlers.ofString());
			if (DEBUG) {
				System.out.printf("DEBUG: %s: %s", response.statusCode(), response.body());
			}
			if(response.statusCode() == 202) {
					System.out.printf(" PASSED ");
				} else {
					System.out.printf(" FAILED %s" + response.statusCode());
				}



		} catch (Exception e) {
			System.out.printf(" FAILED " + e.getMessage());
		}
		System.out.printf("\n");
	}
	
	public void verifyUser() {
		System.out.printf("Test Case 1.3 (verify user OTP) ");

		
		Scanner scanner = new Scanner(System.in);
		System.out.print("OPERATOR!! PLEASE ENTER THE OTP CODE SENT TO EMAIL: ");
		String ONETIMECODE = scanner.nextLine();
		
		
		//String jsonData = "{ \"email\": \"" + EMAIL + "\", \"password\": \"" + PASSWORD + "\" }";
		JSONObject json = new JSONObject();
		json.put("email", EMAIL);  // or just userId if it's a String or UUID
		json.put("password", PASSWORD);
		json.put("oneTimeCode", ONETIMECODE);


		String jsonData = json.toString();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURLString + "/api/auth/verify"))
				.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonData)).build();
		HttpResponse<String> response;
		try {
			response = client.send(request, BodyHandlers.ofString());
			if (DEBUG) {
				System.out.printf("DEBUG: %s: %s", response.statusCode(), response.body());
			}
			if(response.statusCode() == 200) {
			try {
				JSONObject jsonResponse = new JSONObject(response.body());
				jWTtokenString = jsonResponse.getString("jwt");
				userId = jsonResponse.getInt("id");
				if (jWTtokenString != null || jWTtokenString != "") {
					System.out.printf(" PASSED ");
				} else {
					System.out.printf(" FAILED %s" + response.statusCode());
				}
			} catch (Exception e) {
				System.out.printf(" FAILED Exception:%s %s: %s", e, response.statusCode(), response.body());
			}
			} else {
				System.out.printf(" FAILED %s: %s", response.statusCode(), response.body());
			}

		} catch (Exception e) {
			System.out.printf(" FAILED " + e.getMessage());
		}
		System.out.printf("\n");
	}
	
	

	public void addTransaction() {
		System.out.printf("Test Case 2.1 (add Transaction to user) ");

		// create random data for transaction
		Random rand = new Random();

		tranDesc = String.valueOf(rand.nextInt(10000));
		tranAmount = String.valueOf(rand.nextDouble(10000));
		tranCat = String.valueOf(rand.nextInt(10000));
		tranDate = LocalDate.now();
		JSONObject json = new JSONObject();
		json.put("userId", userId);  // or just userId if it's a String or UUID
		json.put("transactionDesc", tranDesc);
		json.put("amount", tranAmount);
		json.put("category", tranCat);
		json.put("date", tranDate);
		json.put("type", "test");

		String jsonData = json.toString();

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseURLString + "/api/transactions/sdata"))
				.header("Authorization", "Bearer " + jWTtokenString).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonData)).build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			if (DEBUG) {
				System.out.printf("DEBUG: %s: %s", response.statusCode(), response.body());
			}
			if (response.statusCode() == 200) {
				System.out.printf(" PASSED ");
			} else {
				System.out.printf(" FAILED %s: %s", response.statusCode(), response.body());
			}
			// System.out.println(response.body());
		} catch (Exception e) {
			System.out.printf(" FAILED ");
			System.out.printf(" Error: %s" + e);
		}
		System.out.printf("\n");
	}

	public void fetchTransaction() {
		System.out.printf("Test Case 2.2 (fetch a transaction from a user) ");
		JSONObject json = new JSONObject();
		json.put("userId", userId);  // or just userId if it's a String or UUID


		String jsonData = json.toString();

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(baseURLString + "/api/transactions/fetch?userId=" + userId))
				.header("Authorization", "Bearer " + jWTtokenString).GET().build();
		HttpResponse<String> response;
		try {
			response = client.send(request, BodyHandlers.ofString());
			if (DEBUG) {
				System.out.printf("DEBUG: %s: %s", response.statusCode(), response.body());
			}
			try {
				JSONArray jsonResponses = new JSONArray(response.body());
				JSONObject jsonResponse = null;
				for (int i = 0; i < jsonResponses.length(); i++) {
					if (jsonResponses.getJSONObject(i).getString("transactionDesc").equals(tranDesc)) {
						jsonResponse = jsonResponses.getJSONObject(i);
						break;
					}
				}
				if (jsonResponse != null) {
					tranId = jsonResponse.getString("id");

					if (jsonResponse.getInt("userId") != (userId)) {
						System.out.printf(" FAILED USERID DOESNT MATCH Expected %d, Got: %d", userId,
								jsonResponse.getInt("userId"));
					} else if (!jsonResponse.getString("transactionDesc").equals(tranDesc)) {
						System.out.printf(" FAILED DESC DOESNT MATCH Expected %s, Got: %s",
								jsonResponse.getString("transactionDesc"), tranDesc);
					} else if (!jsonResponse.getString("amount").equals(tranAmount)) {
						System.out.printf(" FAILED AMOUNT DOESNT MATCH Expected %s, got: %s",
								jsonResponse.getString("amount"), tranAmount);
					} else if (!jsonResponse.getString("category").equals(tranCat)) {
						System.out.printf(" FAILED AMOUNT DOESNT MATCH Expected %s, got: %s",
								jsonResponse.getString("category"), tranCat);
					} else {
						System.out.printf(" PASSED ");
					}
				}
			} catch (Exception e) {
				System.out.printf(" FAILED %s %s: %s", e, response.statusCode(), response.body());
			}

		} catch (Exception e) {
			System.out.printf(" FAILED " + e.getMessage());
		}
		System.out.printf("\n");
	}

	public void deleteTransaction() {
		System.out.printf("Test Case 2.3 (delete Transaction from user) ");
		
		JSONObject json = new JSONObject();
		json.put("id", tranId);
		String jsonData = json.toString();
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://bapi.chirpich.org/api/transactions/delete?id=" + tranId))
				.header("Authorization", "Bearer " + jWTtokenString)
				.GET()
				.build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			if (DEBUG) {
				System.out.printf("DEBUG: %s: %s", response.statusCode(), response.body());
			}
			if (response.statusCode() == 200) {
				System.out.printf(" PASSED ");
			} else {
				System.out.printf(" FAILED %s: %s", response.statusCode(), response.body());
			}
			// System.out.println(response.body());
		} catch (Exception e) {
			System.out.printf(" FAILED ");
			System.out.printf(" Error: %s" + e.toString());
		}
		System.out.printf("\n");
	}
	
	
	public void addCategory() {
		System.out.printf("Test Case 3.1 (add a category to user) ");

		// create random data for transaction
		Random rand = new Random();


		maxSpendAmount = rand.nextDouble(10000);
		tranCat = String.valueOf(rand.nextInt(10000));
		
		JSONObject json = new JSONObject();
		json.put("category", tranCat);
		json.put("maxSpendAmt", maxSpendAmount);
		json.put("userId", userId);

		String jsonData = json.toString();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://bapi.chirpich.org/api/transactions/category/sdata"))
				.header("Authorization", "Bearer " + jWTtokenString).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonData)).build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			if (DEBUG) {
				System.out.printf("DEBUG: %s: %s", response.statusCode(), response.body());
			}
			if (response.statusCode() == 200) {
				System.out.printf(" PASSED ");
			} else {
				System.out.printf(" FAILED %s: %s", response.statusCode(), response.body());
			}
			// System.out.println(response.body());
		} catch (Exception e) {
			System.out.printf(" FAILED ");
			System.out.printf(" Error: %s" + e);
		}
		System.out.printf("\n");
	}
	public void fetchCategory() {
		System.out.printf("Test Case 3.2 (fetch a category from a user) ");
		String jsonData = "{ \"category\": \"" + tranCat + "\" }";
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://bapi.chirpich.org/api/transactions/category/fetch?userId=" + userId))
				.header("Authorization", "Bearer " + jWTtokenString).GET().build();
		HttpResponse<String> response;
		try {
			response = client.send(request, BodyHandlers.ofString());
			if (DEBUG) {
				System.out.printf("DEBUG: %s: %s", response.statusCode(), response.body());
			}
			try {
				JSONArray jsonResponses = new JSONArray(response.body());
				JSONObject jsonResponse = null;
				for (int i = 0; i < jsonResponses.length(); i++) {
					if (jsonResponses.getJSONObject(i).getString("category").equals(tranCat)) {
						jsonResponse = jsonResponses.getJSONObject(i);
						break;
					}
				}
				if (jsonResponse != null) {
					catId = jsonResponse.getString("uuid");

					if (jsonResponse.getInt("userId") != userId) {
						System.out.printf(" FAILED USERID DOESNT MATCH Expected %d, Got: %d", userId,
								jsonResponse.getInt("userId"));
					} else if (!jsonResponse.getString("category").equals(tranCat)) {
						System.out.printf(" FAILED CATEGORY DOESNT MATCH Expected %s, Got: %s",
								jsonResponse.getString("category"), tranCat);
					} else if (!(jsonResponse.getDouble("maxSpendAmt") == maxSpendAmount)) {
						System.out.printf(" FAILED AMOUNT DOESNT MATCH Expected %f, got: %f",
								jsonResponse.getDouble("maxSpendAmt"), maxSpendAmount);
					} else {
						System.out.printf(" PASSED ");
					}
				}
			} catch (Exception e) {
				System.out.printf(" FAILED %s %s: %s", e, response.statusCode(), response.body());
			}

		} catch (Exception e) {
			System.out.printf(" FAILED " + e.getMessage());
		}
		System.out.printf("\n");
	}
	
	
	public void deleteCategory() {
		System.out.printf("Test Case 3.3 (delete category from user) ");

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://bapi.chirpich.org/api/transactions/category/delete?id=" + catId))
				.header("Authorization", "Bearer " + jWTtokenString)
				.GET().build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			if (DEBUG) {
				System.out.printf("DEBUG: %s: %s", response.statusCode(), response.body());
			}
			if (response.statusCode() == 200) {
				System.out.printf(" PASSED ");
			} else {
				System.out.printf(" FAILED %s: %s", response.statusCode(), response.body());
			}
			// System.out.println(response.body());
		} catch (Exception e) {
			System.out.printf(" FAILED ");
			System.out.printf(" Error: %s" + e.toString());
		}
		System.out.printf("\n");
	}
	
	
	
	
	/**
	 * @param dEBUG the dEBUG to set
	 */
	public void setDEBUG(boolean dEBUG) {
		DEBUG = dEBUG;
	}

	/**
	 * @return the baseURLString
	 */
	public String getBaseURLString() {
		return baseURLString;
	}

	/**
	 * @param baseURLString the baseURLString to set
	 */
	public void setBaseURLString(String baseURLString) {
		this.baseURLString = baseURLString;
	}

	/**
	 * @return the eMAIL
	 */
	public String getEMAIL() {
		return EMAIL;
	}

	/**
	 * @param eMAIL the eMAIL to set
	 */
	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}



}