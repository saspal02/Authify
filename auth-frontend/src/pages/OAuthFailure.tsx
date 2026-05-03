import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { AlertTriangle } from "lucide-react";
import { useNavigate } from "react-router";

function OAuthFailure() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex items-center justify-center p-6">
      <Card className="w-full max-w-md">
        <CardHeader className="flex flex-col items-center text-center gap-2">
          <div className="p-3 rounded-full bg-red-100">
            <AlertTriangle className="text-red-600 w-6 h-6" />
          </div>

          <CardTitle className="text-xl">Login Failed</CardTitle>

          <CardDescription>
            We were unable to complete your authentication. Please try again.
          </CardDescription>
        </CardHeader>

        <CardContent className="flex flex-col gap-3">
          <Button className="w-full" onClick={() => navigate("/login")}>
            Back to Login
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}

export default OAuthFailure;
