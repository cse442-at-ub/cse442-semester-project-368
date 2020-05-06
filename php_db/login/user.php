<?php
    
    include_once 'db-connect.php';
    
    class User{
        
        private $db;
        
        private $db_table = "users";
        
        private $name = "something2";

        public function __construct(){
            $this->db = new DbConnect();
        }
        
        public function isLoginExist($email, $password){
            
            $query = "select * from ".$this->db_table." where email = '$email' AND password = '$password' Limit 1";
            
            $result = mysqli_query($this->db->getDb(), $query);
            
            if(mysqli_num_rows($result) > 0){
                $row = $result->fetch_assoc();
                $this->name = $row['name'];
                
                return true;
                
            }
            
            mysqli_close($this->db->getDb());
            
            return false;
            
        }
        
        public function isEmailUsernameExist($email){
            
            $query = "select * from ".$this->db_table." where email = '$email'";
            
            $result = mysqli_query($this->db->getDb(), $query);
            
            if(mysqli_num_rows($result) > 0){
                
                mysqli_close($this->db->getDb());
                
                return true;
                
            }
               
            return false;
            
        }
        
        public function isValidEmail($email){
            return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
        }
        
        public function createNewRegisterUser($email, $password, $name){
              
            $isExisting = $this->isEmailUsernameExist($email);
            
            if($isExisting){
                
                $json['success'] = 0;
                $json['message'] = "Error in registering. Email already exists";
            }
            
            else{
                
            $isValid = $this->isValidEmail($email);
                
                if($isValid)
                {
                $query = "insert into ".$this->db_table." (email, password, created_at, updated_at, name) values ('$email', '$password', NOW(), NOW(), '$name')";
                
                $inserted = mysqli_query($this->db->getDb(), $query);
                
                if($inserted == 1){
                    
                    $json['success'] = 1;
                    $json['name'] = $name;
                    $json['message'] = "Successfully registered";
                    
                }else{
                    
                    $json['success'] = 0;
                    $json['message'] = "Error in registering. Probably the email already exists";
                    
                }
                
                mysqli_close($this->db->getDb());
                }
                else{
                    $json['success'] = 0;
                    $json['message'] = "Error in registering. Email Address is not valid";
                }
                
            }
            
            return $json;
            
        }
        
        public function loginUsers($email, $password){
            
            $json = array();
            
            $canUserLogin = $this->isLoginExist($email, $password);
          

            if($canUserLogin){
                $json['success'] = 1;
                $json['message'] = "Successfully logged in";
                $json['name'] = $this->name;
                
            }else{
                $json['success'] = 0;
                $json['message'] = "Incorrect details";
            }
            return $json;
        }


        public function changePassword($email, $password, $newPassword) {
            
            $json = array();
            
            $canUserLogin = $this->isLoginExist($email, $password);
          

            if($canUserLogin){
                $query = "update `users` set `password` = '$newPassword' where `email` = '$email'";
                mysqli_query($this->db->getDb(), $query);
                $json['success'] = 1;
                $json['message'] = "Password changed successfully";
                
            }else{
                $json['success'] = 0;
                $json['message'] = "Incorrect details";
            }
            return $json;


        }
    }
?>
