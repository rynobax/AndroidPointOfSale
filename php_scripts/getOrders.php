<?php
	$servername = "mysql1.cs.clemson.edu";
	$username = "pizza_7187";
	$password = "grandnox9";
	$dbname = "pizza_im5o";

	// Create connection
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
    		die("Connection failed: " . $conn->connect_error);
	}

	$result=$conn->query("select * from my_order");

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
$conn->close();
?>
