import { Button } from "./ui/button";
import { NavLink } from "react-router";
import { FcGoogle } from "react-icons/fc";
import { FaGithub } from "react-icons/fa";

function OAuth2Buttons() {
  const baseUrl = import.meta.env.VITE_BASE_URL || "http://localhost:8083";

  return (
    <div className="space-y-3">
      <NavLink to={`${baseUrl}/oauth2/authorization/google`} className="block">
        <Button
          type="button"
          variant="outline"
          className="w-full cursor-pointer flex items-center gap-3 rounded-2xl"
        >
          <FcGoogle className="h-5 w-5" />
          Continue with Google
        </Button>
      </NavLink>

      <NavLink to={`${baseUrl}/oauth2/authorization/github`} className="block">
        <Button
          type="button"
          variant="outline"
          className="w-full flex cursor-pointer items-center gap-3 rounded-2xl"
        >
          <FaGithub className="h-5 w-5" />
          Continue with GitHub
        </Button>
      </NavLink>
    </div>
  );
}

export default OAuth2Buttons;
