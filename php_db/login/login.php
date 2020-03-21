<?php
    
    require_once 'user.php';
    
    $register = "";
    
    $password = "";
    
    $email = "";
    
    if(isset($_POST['register'])){
        
        $register = $_POST['register'];
        
    }
    
    if(isset($_POST['password'])){
        
        $password = $_POST['password'];
        
    }
    
    if(isset($_POST['email'])){
        
        $email = $_POST['email'];
        
    }
    
    $userObject = new User();
    
    // Registration
    
    if(!empty($email) && !empty($password) && !empty($register)){
        
        $hashed_password = md5($password);
        
        $json_registration = $userObject->createNewRegisterUser($email, $hashed_password);
        
        echo json_encode($json_registration);
        
    }
    
    // Login
    
    if(!empty($email) && !empty($password) && empty($register)){
        
        $hashed_password = md5($password);
        
        $json_array = $userObject->loginUsers($email, $hashed_password);
        
        echo json_encode($json_array);
    }
    ?>