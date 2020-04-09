<?php

include 'DatabaseConfig.php';

 $con = mysqli_connect($HostName,$HostUser,$HostPass,$DatabaseName);

 $email_a=$_POST['email_a'];
 $email_b=$_POST['email_b'];

 $Sql_Query = "insert into friend_data (email_a,email_b) values ('$email_a','$email_b')";
 
 if(mysqli_query($con,$Sql_Query)){
 
 echo 'Data Inserted Successfully';
 
 }
 else{
 
 echo 'Try Again';
 
 }
 mysqli_close($con);
?>