<?php
    $host = 'tethys.cse.buffalo.edu';
    $username = "jiwoncho"; # MySQL username
    $password = '50283501'; # MySQL password
   	$dbname = 'cse442_542_2020_spring_teamw_db';  # DATABASE name

   	$id = "";
    
	if(isset($_POST['id'])){
        
        $id = $_POST['id'];
        
    }
	// Create connection
	$conn = new mysqli($host, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	}

	// sql to delete a record
	$sql = "DELETE FROM friend_data WHERE id = '$id'";

	if ($conn->query($sql) === TRUE) {
	    echo "Friend deleted successfully";
	} else {
	    echo "Error deleting schedule: " . $conn->error;
	}

$conn->close();
?>