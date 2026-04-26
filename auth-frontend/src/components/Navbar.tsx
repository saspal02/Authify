import { NavLink } from "react-router";
import { Button } from "./ui/button";
import { Lock } from "lucide-react";

function Navbar() {
  return (
    <nav className="py-5  dark:border-b border-gray-700 md:py-0 flex md:flex-row gap-4 md:gap-0 flex-col md:h-14 justify-around items-center ">
      {/*brand*/}
      <div className="font-semibold items-center flex gap-2">
        <span className="inline-block text-center h-8 w-8 rounded-md">
          <Lock className="h-6 w-8" />
        </span>
        <span className="text-base tracking-tight">Authify</span>
      </div>

      <div className="flex gap-4 items-center">
        <NavLink to={"/"}>Home</NavLink>
        <NavLink to={"/login"}>
          <Button size={"sm"} className="cursor-pointer" variant={"outline"}>
            Login
          </Button>
        </NavLink>
        <NavLink to={"/SignUp"}>
          <Button size={"sm"} className="cursor-pointer" variant={"outline"}>
            Sign Up
          </Button>
        </NavLink>
      </div>
    </nav>
  );
}

export default Navbar;
