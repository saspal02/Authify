import type RegisterData from "@/models/RegisterData";
import apiClient from "@/config/ApiClient";

//register function
export const registerUser = async (signupData: RegisterData) => {
  // api  call to server to save data
  const response = await apiClient.post(`/auth/register`, signupData);
  return response.data;
};

// get current login user

//refresh token
