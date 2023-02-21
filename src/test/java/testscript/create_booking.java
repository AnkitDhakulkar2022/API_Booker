package testscript;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import constant.status_code;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojo.request.createbooking.Bookingdates;
import pojo.request.createbooking.createbookingRequest;

//Given: all input details -> URL, Headers, path/ query paramerters, payload, 
//When -> submit the API[headerType,endpoint]
//Then -> validate the API 

public class create_booking {

	String tokenid;
	int bookingid;
	createbookingRequest cbr = new createbookingRequest();

	@BeforeMethod
	public void generatetoken() {
		RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
		Response res = RestAssured.given().log().all() // to print request header
				.header("Content-Type", "application/json")
				.body("{\r\n" + "    \"username\" : \"admin\",\r\n" + "    \"password\" : \"password123\"\r\n" + "}")
				.when().post("/auth") // /auth is endpoint
				.then().log().all() // to print response header
				.extract() // require to print response
				.response(); // require to print response

		System.out.println(res.statusCode());
		Assert.assertEquals(res.statusCode(), status_code.ok);
		System.out.println(res.asPrettyString());
		tokenid = res.jsonPath().get("token");
		System.out.println("Token id is =" + tokenid);

	}

	@Test
	public void createbookingTest() {

		Bookingdates bd = new Bookingdates();
		bd.setCheckin("2023-05-19");
		bd.setCheckout("2023-05-20");

		//createbookingRequest cbr = new createbookingRequest();
		cbr.setFirstname("john");
		cbr.setLastname("brown");
		cbr.setTotalprice(125);
		cbr.setDepositpaid(true);
		cbr.setAdditionalneeds("breakfast");

		cbr.setBookingdates(bd);

		Response res = RestAssured.given()
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.body(cbr)
				.log().all()
				.when()
				.post("/booking");

		Assert.assertEquals(res.statusCode(), status_code.ok);
		bookingid=res.jsonPath().getInt("bookingid");
		Assert.assertTrue(bookingid>0);
//		Assert.assertEquals(res.jsonPath().getString("booking.firstname"),cbr.getFirstname());
//		Assert.assertEquals(res.jsonPath().getString("booking.lastname"),cbr.getLastname());
//		Assert.assertEquals(res.jsonPath().getInt("booking.totalprice"),cbr.getTotalprice());
//		Assert.assertEquals(res.jsonPath().getBoolean("booking.depositpaid"),cbr.isDepositpaid());
//		Assert.assertEquals(res.jsonPath().getString("booking.additionalneeds"),cbr.getAdditionalneeds());
//		Assert.assertEquals(res.jsonPath().getString("booking.bookingdates.checkin"),cbr.getBookingdates().getCheckin());
//		Assert.assertEquals(res.jsonPath().getString("booking.bookingdates.checkout"),cbr.getBookingdates().getCheckout());
		validateResponse(res,cbr,"booking.");
	}

	@Test(priority=1, enabled=false)
	public void getbooking() {
		//int bookingid=4706;
		Response res = RestAssured.given()
				
				.header("Accept", "application/json")
				.log().all()
				.when()
				.get("/booking/"+bookingid);
		Assert.assertEquals(res.statusCode(), status_code.ok);
		System.out.println(res.asPrettyString());
//		List<Integer>listofBookingID= res.jsonPath().getList("bookingID");
//		System.out.println(listofBookingID.size());
//		Assert.assertTrue(listofBookingID.contains(bookingid));
		validateResponse(res, cbr, "");
	
	}
	
	@Test(priority =2,enabled=false)
	public void getbookingDeserilizedTest() {
		
		Response res = RestAssured.given()
				
				.header("Accept", "application/json")
				.log().all()
				.when()
				.get("/booking/"+bookingid);
		Assert.assertEquals(res.statusCode(), status_code.ok);
		System.out.println(res.asPrettyString());
		createbookingRequest resbody=res.as(createbookingRequest.class);
		System.out.println(resbody);
		
		Assert.assertTrue(resbody.equals(cbr)); // with this method no need to write all the assertion again 
		
	}
	
	@Test(priority =3,enabled=false)
	public void updatebookingIDTest() {
		cbr.setFirstname("Pankaj");
		Response res = RestAssured.given()
				.header("Content-Type","application/json")
				.header("Accept", "application/json")
				.header("Cookie","token="+tokenid)
				
				.log().all()
				.body(cbr)
				.when()
				.put("/booking/"+bookingid);
		Assert.assertEquals(res.statusCode(), status_code.ok);
		System.out.println(res.asPrettyString());
		createbookingRequest resbody=res.as(createbookingRequest.class);
		System.out.println(resbody);
		Assert.assertTrue(resbody.equals(cbr)); // with this method no need to write all the assertion again 
		
	}
	
	@Test(priority =4,enabled=false)
	public void PartialupdatebookingIDTest() {
		cbr.setFirstname("Rahul");
		cbr.setLastname("patil");
		Response res = RestAssured.given()
				.header("Content-Type","application/json")
				.header("Accept", "application/json")
				.header("Cookie","token="+tokenid)
				
				.log().all()
				.body(cbr)
				.when()
				.patch("/booking/"+bookingid);
		Assert.assertEquals(res.statusCode(), status_code.ok);
		System.out.println(res.asPrettyString());
		createbookingRequest resbody=res.as(createbookingRequest.class);
		System.out.println(resbody);
		Assert.assertTrue(resbody.equals(cbr)); // with this method no need to write all the assertion again 
		
	}
	
	@Test(priority =5)
	public void DeletebookingIDTest() {
		Response res = RestAssured.given()
				.header("Content-Type","application/json")
				.header("Cookie","token="+tokenid)
				//.log().all()
				.body(cbr)
				.when()
				.delete("/booking/"+bookingid);
		Assert.assertEquals(res.statusCode(), status_code.created);
		System.out.println(res.asPrettyString());
		//createbookingRequest resbody=res.as(createbookingRequest.class);
		//System.out.println(resbody);
		//Assert.assertTrue(resbody.equals(cbr)); // with this method no need to write all the assertion again 
		
	}
	private void validateResponse(Response res, createbookingRequest cbr, String object) {
		
		Assert.assertEquals(res.jsonPath().getString(object+"firstname"),cbr.getFirstname());
		Assert.assertEquals(res.jsonPath().getString(object+"lastname"),cbr.getLastname());
		Assert.assertEquals(res.jsonPath().getInt(object+"totalprice"),cbr.getTotalprice());
		Assert.assertEquals(res.jsonPath().getBoolean(object+"depositpaid"),cbr.isDepositpaid());
		Assert.assertEquals(res.jsonPath().getString(object+"additionalneeds"),cbr.getAdditionalneeds());
		Assert.assertEquals(res.jsonPath().getString(object+"bookingdates.checkin"),cbr.getBookingdates().getCheckin());
		Assert.assertEquals(res.jsonPath().getString(object+"bookingdates.checkout"),cbr.getBookingdates().getCheckout());
	}
//below are the normal code without bdd
//	@Test
//	public void createbookingTestWithOutBDDPlaneMode() {
//		String payload = "{\r\n" + "    \"username\" : \"admin\",\r\n" + "    \"password\" : \"password123\"\r\n" + "}";
//		RequestSpecification res1 = RestAssured.given();
//		res1.baseUri("https://restful-booker.herokuapp.com/");
//		res1.header("Content-Type", "application/json");
//		res1.body(payload);
//		Response res2 = res1.post("/auth");
//		System.out.println(res2.statusCode());
//		Assert.assertEquals(res2.statusCode(), 200);
//		System.out.println(res2.asPrettyString());
//	}

}
