<?php

include 'DatabaseConfig.php';

 $con = mysqli_connect($HostName,$HostUser,$HostPass,$DatabaseName);

 $sender_name=$_POST['sender_name'];
 $sender_email=$_POST['sender_email'];
 $receiver_name=$_POST['receiver_name'];
 $receiver_email=$_POST['receiver_email'];
 $status=$_POST['status'];

 $Sql_Query = "insert into friend_request_data (sender_name,sender_email,receiver_name,receiver_email,status) values ('$sender_name','$sender_email','$receiver_name','$receiver_email','$status')";
 
 if(!empty($sender_name) and mysqli_query($con,$Sql_Query)){
 echo 'Data Inserted Successfully';
 }
 elseif (empty($sender_name)) {
 echo "Data cannot be inserted directly through html.";
 } 
else {
 echo 'Try Again';
 }

 mysqli_close($con);
?>