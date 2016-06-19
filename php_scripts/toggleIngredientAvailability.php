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
if (isset($objs->{'id'})) {

	$id = $objs->{'id'};

	$servername = "mysql1.cs.clemson.edu";
        $username = "pizza_7187";
        $password = "grandnox9";
        $dbname = "pizza_im5o";

        // Create connection
        $conn = new mysqli($servername, $username, $password, $dbname);


    // mysql inserting a new row
	$editSQL = "UPDATE ingredient SET available = NOT available WHERE id = $id";
    	$result = $conn->query($editSQL);

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Product successfully toggled.";
 
        // echoing JSON response
        echo json_encode($response);
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
