@validacion
Feature: Validación de integridad del API de Store en PetStore

  Background:

    Given la URL base del API es "https://petstore.swagger.io/v2"
  @validacion1
  Scenario Outline: Creación de una nueva orden
    When realizo una petición POST a "/store/order":
      """
      {
        "id": <orderId>,
        "petId": <petId>,
        "quantity": <quantity>,
        "shipDate": "2024-12-15T10:15:30.000+00:00",
        "status": "placed",
        "complete": true
      }
      """
    Then el código de respuesta debe ser 200
    And el cuerpo de la respuesta debe contener:
      | key      | value      |
      | id       | <orderId>  |
      | petId    | <petId>    |
      | quantity | <quantity> |
      | status   | placed     |

    Examples:
      | orderId | petId | quantity |
      | 1       | 501   | 5        |
      | 2       | 502   | 3        |

  @validacion2
  Scenario Outline: Consulta de una orden existente

    Given la orden con id <orderId>, petId <petId>, quantity <quantity> ha sido creada previamente
    When realizo una petición GET a "/store/order/<orderId>"
    Then el código de respuesta debe ser 200
    And el cuerpo de la respuesta debe contener:
      | key      | value      |
      | id       | <orderId>  |
      | petId    | <petId>    |
      | quantity | <quantity> |

    Examples:
      | orderId | petId | quantity |
      | 1       | 501   | 5        |
      | 2       | 502   | 3        |

