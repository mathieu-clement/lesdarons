function pizzaController($scope, $http) {
    $scope.pizzas = [];

    $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
    $http.defaults.transformRequest = function(obj) {
        var str = [];
        for(var p in obj)
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        return str.join("&");
    };

    var BASE_URL = "http://localhost:8080/pizzaorders-ws/rest";

    $scope.orders = [];

    //$scope.$on('$viewContentLoaded', function() {
    $scope.getPizzas = function () {
        $http.get(BASE_URL + "/pizzas")
            .success(function (data, status, headers, config) {
                $scope.pizzas = data;
            })
            .error(function (data, status, headers, config) {
                alert("Error getting pizzas...");
            });
    };
    //});

    $scope.basket = [];

    $scope.total = 0;

    $scope.firstName = "";
    $scope.lastName = "";
    $scope.streetName = "";
    $scope.houseNumber = "";
    $scope.postalCode = "";
    $scope.city = "";
    $scope.country = "Suisse";



    $scope.addPizza = function(pizza) {
        $scope.basket.push(pizza);
        $scope.total += pizza.priceBig;
    };

    $scope.sendOrder = function () {
        $http.post(BASE_URL + "/orders/begin",
            {
                firstName: $scope.firstName,
                lastName: $scope.lastName,
                streetName: $scope.streetName,
                houseNumber: $scope.houseNumber,
                postalCode: $scope.postalCode,
                city: $scope.city,
                country: $scope.country
            })
            .success(function (data, status, headers, config) {
                var orderId = data.id;

                $scope.basket.forEach(function(element, index, array) {

                    $http.post(BASE_URL + "/orders/" + orderId + "/1/big/" + element.name,
                        {})
                        .success(function (data, status, headers, config) {
                            console.log("Ajouté " + element.name);
                        })
                        .error(function (data, status, headers, config) {
                            alert("Errer ajout pizza à la commande...");
                        });
                });

                $http.post(BASE_URL + "/orders/" + orderId + "/confirm",
                    {})
                    .success(function (data, status, headers, config) {
                        alert("Commande bien reçue");
                    })
                    .error(function (data, status, headers, config) {
                        alert("Error confirm order...");
                    });
            })
            .error(function (data, status, headers, config) {
                alert("Error begin order...");
            });
        // country = "Suisse"
    };

    $scope.updateOrders = function () {

    };

    $scope.getPizzas();
}