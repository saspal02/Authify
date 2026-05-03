import useAuth from "@/auth/store";
import { Spinner } from "@/components/ui/spinner";
import { refreshToken } from "@/services/AuthService";
import { useEffect, useRef, useState } from "react";
import toast from "react-hot-toast";
import { useNavigate } from "react-router";
import OAuthFailure from "./OAuthFailure";

function OAuthSuccess() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const hasRun = useRef(false);

  const changeLocalLoginData = useAuth((state) => state.changeLocalLoginData);
  const navigate = useNavigate();

  useEffect(() => {
    if (hasRun.current) return;
    hasRun.current = true;

    async function getAccessToken() {
      try {
        const responseLoginData = await refreshToken();

        changeLocalLoginData(
          responseLoginData.accessToken,
          responseLoginData.user,
          true,
        );

        toast.success("Login success!");
        navigate("/dashboard", { replace: true });
      } catch (err) {
        console.log(err);
        toast.error("Authentication failed");
        setError(true);
      } finally {
        setLoading(false);
      }
    }

    getAccessToken();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col gap-3 justify-center items-center">
        <Spinner />
        <h1 className="text-2xl font-semibold">Please wait....</h1>
      </div>
    );
  }

  if (error) {
    return <OAuthFailure />;
  }

  return null;
}

export default OAuthSuccess;
