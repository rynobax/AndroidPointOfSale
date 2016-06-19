<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

$json = file_get_contents('php://input');
$objs = json_decode($json);

// check for required fields
if (isset($objs->{'order'}) && isset($objs->{'price'})) {
 
    	$order = json_decode($objs->{'order'}, true);
	$price = json_decode($objs->{'price'});
 
	$servername = "mysql1.cs.clemson.edu";
        $username = "pizza_7187";
        $password = "grandnox9";
        $dbname = "pizza_im5o";

        // Create connection
        $conn = new mysqli($servername, $username, $password, $dbname);

	//print_r($order);
	$current = date('Y-m-d H:i:s');
	$addOrderSQL = "INSERT INTO my_order (ordered_when, price) VALUES (now(), '$price')";
	$result = $conn->query($addOrderSQL);
	$order_id = $conn->insert_id;
	foreach($order as $pizza){
		$addPizzaSQL = "INSERT INTO pizza (order_id) VALUES ('$order_id')";
		$conn->query($addPizzaSQL);
		$pizza_id = $conn->insert_id;
		foreach($pizza as $ingredient_id){
			$addPizzaIngredientSQL = "INSERT INTO pizza_ingredient (pizza_id, ingredient_id) VALUES ('$pizza_id', '$ingredient_id')";
			$conn->query($addPizzaIngredientSQL);
		}
	}

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        //echo $mysqli->insert_id;
	echo "Success?";
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
	$response["error"] = $conn->error;
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Wrong arguments";

    // echoing JSON response
    echo json_encode($response);
}
?>
