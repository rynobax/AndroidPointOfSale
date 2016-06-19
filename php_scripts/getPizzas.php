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
if (isset($objs->{'order_id'})) {

    	$order_id = $objs->{'order_id'};

	$servername = "mysql1.cs.clemson.edu";
        $username = "pizza_7187";
        $password = "grandnox9";
        $dbname = "pizza_im5o";

        // Create connection
        $conn = new mysqli($servername, $username, $password, $dbname);



    // mysql inserting a new row
	$SQL = "SELECT pi.pizza_id, pi.ingredient_id 
	FROM pizza_ingredient pi 
	INNER JOIN pizza p 
		on pi.pizza_id = p.id 
	WHERE p.order_id = '$order_id'";
    $result = $conn->query($SQL);
 
    // check if row inserted or not
    if ($result) {
 
        // echoing JSON response
        $items = array();
        if ($result->num_rows > 0) {
                // output data of each row
                while($item = $result->fetch_assoc()) {
                        array_push($items, $item);
                }
                echo json_encode($items);
        } else {
                echo "0 results";
	}
	}else{
		echo "Error";
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Wrong arguments";

    // echoing JSON response
    echo json_encode($response);
}
?>
