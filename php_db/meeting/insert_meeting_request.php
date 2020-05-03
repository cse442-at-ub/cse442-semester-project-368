<?php

include 'DatabaseConfig.php';

 $con = mysqli_connect($HostName,$HostUser,$HostPass,$DatabaseName);

 $sender_name=$_POST['sender_name'];
 $sender_email=$_POST['sender_email'];
 $receiver_name=$_POST['receiver_name'];
 $receiver_email=$_POST['receiver_email'];
 $status=$_POST['status'];
 $title=$_POST['title'];
 $start_date=$_POST['start_date'];
 $start_time=$_POST['start_time'];
 $end_date=$_POST['end_date'];
 $end_time=$_POST['end_time'];
 $description=$_POST['description'];
 $participant=$_POST['participant'];

 $Sql_Query = "insert into meeting_request_data (sender_name,sender_email,receiver_name,receiver_email,status,title,start_date,start_time,end_date,end_time,description,participant) values ('$sender_name','$sender_email','$receiver_name','$receiver_email','$status','$title','$start_date','$start_time','$end_date','$end_time','$description','$participant')";
 
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