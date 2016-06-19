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
if (isset($objs->{'my_key'})) {

    	$my_key = $objs->{'my_key'};

	$servername = "mysql1.cs.clemson.edu";
        $username = "pizza_7187";
        $password = "grandnox9";
        $dbname = "pizza_im5o";

        // Create connection
        $conn = new mysqli($servername, $username, $password, $dbname);



	// mysql inserting a new row
	$insertSQL = "SELECT my_value FROM setting WHERE my_key = '$my_key'";
	$result = $conn->query($insertSQL);

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $items = array();
        if ($result->num_rows > 0) {
                 //output data of each row
                while($item = $result->fetch_assoc()) {
                        array_push($items, $item);
                }
                echo json_encode($item);
        } else {
                echo "0 results";
        }
 
        // echoing JSON response
        echo json_encode($items);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
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
