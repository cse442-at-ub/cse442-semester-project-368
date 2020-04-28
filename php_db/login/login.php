<?php
    
    require_once 'user.php';
    
    $register = "";
    
    $password = "";
    
    $email = "";

    $name = "";

    $newPassword = "";
    
    if(isset($_POST['register'])){
        
        $register = $_POST['register'];
        
    }
    
    if(isset($_POST['password'])){
        
        $password = $_POST['password'];
        
    }
    
    if(isset($_POST['email'])){
        
        $email = $_POST['email'];
        
    }
    
    if(isset($_POST['name'])){
        
        $name = $_POST['name'];
        
    }

    if(isset($_POST['newPassword'])){
        
        $newPassword = $_POST['newPassword'];
        
    }
    
    $userObject = new User();
    
    // Registration
    
    if(!empty($email) && !empty($password) && !empty($register) && empty($newPassword)){
        
        $hashed_password = md5($password);
        
        $json_registration = $userObject->createNewRegisterUser($email, $hashed_password, $name);
        
        echo json_encode($json_registration);
        
    }
    
    // Login
    
    if(!empty($email) && !empty($password) && empty($register) && empty($newPassword)){
        
        $hashed_password = md5($password);
        
        $json_array = $userObject->loginUsers($email, $hashed_password);
        
        echo json_encode($json_array);
    }

    // Change password
    
    if(!empty($email) && !empty($password) && empty($register) && !empty($newPassword)){
        
        $hashed_password = md5($password);
        $hashed_new_password = md5($newPassword);
        
        $json_array = $userObject->changePassword($email, $hashed_password, $hashed_new_password);
        
        echo json_encode($json_array);
    }
?>
