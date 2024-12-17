package nttdata.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.*;

public class ValidarStep {

    private static final Logger logger = LoggerFactory.getLogger(ValidarStep.class);
    private String baseURL;
    private Response response;
// LAS SIGUIENTES LINEAS SON PARA REALIZAR LA CREACION DE OBJETOS
    @Given("la URL base del API es {string}")
    public void URLBase(String url) {
        baseURL = url;
        RestAssured.baseURI = baseURL;
        logger.info("URL base del API: {}", baseURL);
    }

    @When("realizo una petición POST a {string}:")
    public void realizarPeticionPOST(String endpoint, String body) {
        logger.info("Enviando petición POST a: {}", endpoint);
        logger.debug("Cuerpo de la petición: {}", body);

        response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(endpoint);
        logger.info("Respuesta del servidor: {}", response.getBody().asString());
    }

    @Then("el código de respuesta debe ser {int}")
    public void codigoDeRespuesta(int statusCode) {
        logger.info("Validando código de respuesta. Esperado: {}", statusCode);
        response.then().statusCode(statusCode);
        logger.info("Código de respuesta validado: {}", statusCode);
    }

    @Then("el cuerpo de la respuesta debe contener:")
    public void cuerpoDeRespuesta(io.cucumber.datatable.DataTable dataTable) {
        logger.info("Validando el cuerpo de la respuesta...");

        dataTable.asMaps(String.class, String.class).forEach(row -> {
            String key = (String) row.get("key");
            String value = row.get("value").toString();
            logger.debug("Validando: {} = {}", key, value);

            try {
                if (value.matches("\\d+")) { // Si es un número
                    response.then().body(key, equalTo(Integer.parseInt(value)));
                    logger.info("Valor para {} validado como: {}", key, value);
                } else { // Si es un string
                    response.then().body(key, equalTo(value));
                    logger.info("Valor para {} validado como: {}", key, value);
                }
            } catch (Exception e) {
                logger.error("Error al validar {} con valor {}", key, value, e);
                throw e;
            }
        });
    }



    // LAS SIGUIENTES LINEAS SON PARA REALIZAR CONSULTAS

    @Given("la orden con id {}, petId {}, quantity {} ha sido creada previamente")
    public void validarPeticionPOST(int orderId, int petId, int quantity) {

        String body = String.format(
                "{\n" +
                        "  \"id\": " + orderId + ",\n" +
                        "  \"petId\": " + petId + " ,\n" +
                        "  \"quantity\": " + quantity + ",\n" +
                        "  \"shipDate\": \"2024-12-15T10:15:30.000+00:00\",\n" +
                        "  \"status\": \"placed\",\n" +
                        "  \"complete\": true\n" +
                        "}");

        logger.info("Creando orden con ID: {}", orderId);
        logger.debug("Cuerpo de la petición para crear la orden: {}", body);

        response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/store/order");

        response.then().statusCode(200);
        logger.info("Orden creada exitosamente con ID: {}", orderId);
    }
    @When("realizo una petición GET a {string}")
    public void realizoUnaPeticionGET(String endpoint) {
        logger.info("Realizando petición GET a: {}", endpoint);

        response = RestAssured
                .given()
                .get(endpoint);

        logger.info("Respuesta del GET: {}", response.getBody().asString());
    }
}
