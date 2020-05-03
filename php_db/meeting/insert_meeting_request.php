<?php

include 'DatabaseConfig.php';

 $con = mysqli_connect($HostName,$HostUser,$HostPass,$DatabaseName);

 $email_a=$_POST['sender_name'];
 $email_a=$_POST['sender_email'];
 $email_b=$_POST['receiver_name'];
 $email_b=$_POST['receiver_email'];
 $email=$_POST['status'];
 $title=$_POST['title'];
 $start_date=$_POST['start_date'];
 $start_time=$_POST['start_time'];
 $end_date=$_POST['end_date'];
 $end_time=$_POST['end_time'];
 $description=$_POST['description'];

 $Sql_Query = "insert into meeting_request_data (sender_name,sender_email,receiver_name,receiver_email,status,title,start_date,start_time,end_date,end_time,description) values ('$sender_name','$sender_email','$receiver_name','$receiver_email','$status','$title','$start_date','$start_time','$end_date','$end_time','$description')";
 
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