import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { motion } from "framer-motion";
import { User, ShieldCheck, Activity, Mail } from "lucide-react";
import { getCurrentUser } from "@/services/AuthService";
import useAuth from "@/auth/store";
import { useEffect, useState } from "react";
import type UserT from "@/models/User";
import toast from "react-hot-toast";

function Userhome() {
  const authUser = useAuth((state) => state.user);
  const [userData, setUserData] = useState<UserT | null>(null);
  const [loading, setLoading] = useState(false);

  // ✅ Helper to format roles nicely
  const formatRole = (role: string) => {
    const clean = role.replace("ROLE_", "").toLowerCase();
    return clean.charAt(0).toUpperCase() + clean.slice(1);
  };

  const fetchUser = async () => {
    if (!authUser?.email) return;

    setLoading(true);
    try {
      const data = await getCurrentUser(authUser.email);
      setUserData(data);
      toast.success("User data fetched");
    } catch (err) {
      console.error(err);
      toast.error("Failed to fetch user");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUser();
  }, []);

  return (
    <div className="min-h-screen bg-background text-foreground p-6">
      {/* Title */}
      <motion.h1
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-3xl font-bold mb-6"
      >
        Dashboard
      </motion.h1>

      {/* USER PROFILE */}
      <Card className="mb-6 rounded-2xl">
        <CardContent className="p-6 flex items-center gap-4">
          <User className="w-10 h-10 text-primary" />
          <div>
            <h2 className="text-xl font-semibold">
              {userData?.name || "Loading..."}
            </h2>

            <p className="text-muted-foreground flex items-center gap-2">
              <Mail className="w-4 h-4" />
              {userData?.email || authUser?.email}
            </p>

            <p className="text-sm text-muted-foreground">
              Role:{" "}
              {userData?.roles?.length
                ? userData.roles.map((r) => formatRole(r.name)).join(", ")
                : "N/A"}
            </p>
          </div>
        </CardContent>
      </Card>

      {/* SECURITY + AUTH INFO */}
      <div className="grid md:grid-cols-3 gap-6 mb-6">
        <Card>
          <CardContent className="p-6 flex items-center gap-4">
            <ShieldCheck className="w-8 h-8 text-primary" />
            <div>
              <p className="text-sm text-muted-foreground">Auth Provider</p>
              <h3 className="text-lg font-semibold">
                {authUser?.provider || "LOCAL"}
              </h3>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6 flex items-center gap-4">
            <Activity className="w-8 h-8 text-primary" />
            <div>
              <p className="text-sm text-muted-foreground">User ID</p>
              <h3 className="text-lg font-semibold">{userData?.id || "—"}</h3>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6 flex items-center gap-4">
            <User className="w-8 h-8 text-primary" />
            <div>
              <p className="text-sm text-muted-foreground">Status</p>
              <h3 className="text-lg font-semibold">
                {authUser ? "Authenticated" : "Not Logged In"}
              </h3>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* ACTIONS */}
      <div className="flex gap-4">
        <Button onClick={fetchUser} disabled={loading}>
          {loading ? "Loading..." : "Refresh User"}
        </Button>
      </div>
    </div>
  );
}

export default Userhome;
