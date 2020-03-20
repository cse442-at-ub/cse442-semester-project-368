<?php

include 'DatabaseConfig.php';

 $con = mysqli_connect($HostName,$HostUser,$HostPass,$DatabaseName);

 $email=$_POST['email'];
 $title=$_POST['title'];
 $start_date=$_POST['start_date'];
 $start_time=$_POST['start_time'];
 $end_date=$_POST['end_date'];
 $end_time=$_POST['end_time'];
 $description=$_POST['description'];

 $Sql_Query = "insert into schedule_data (email,title,start_date,start_time,end_date,end_time,description) values ('$email','$title','$start_date','$start_time','$end_date','$end_time','$description')";
 
 if(mysqli_query($con,$Sql_Query)){
 
 echo 'Data Inserted Successfully';
 
 }
 else{
 
 echo 'Try Again';
 
 }
 mysqli_close($con);
?>