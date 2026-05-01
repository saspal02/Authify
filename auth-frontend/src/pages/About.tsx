import { Card, CardContent } from "@/components/ui/card";
import { motion } from "framer-motion";
import { Shield, Lock, Globe, Users, Rocket, CheckCircle2 } from "lucide-react";

export default function AboutPage() {
  return (
    <div className="min-h-screen bg-background text-foreground overflow-hidden">
      {/* Hero Section */}
      <section className="py-28 px-6 text-center">
        <motion.h1
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
          className="text-5xl md:text-7xl font-bold tracking-tight"
        >
          About Authify
        </motion.h1>

        <motion.p
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2, duration: 0.8 }}
          className="mt-6 max-w-3xl mx-auto text-lg md:text-xl text-muted-foreground"
        >
          Authify is a modern authentication platform designed to help
          developers secure applications with speed, reliability, and a clean
          developer experience.
        </motion.p>
      </section>

      {/* Mission Section */}
      <section className="py-20 px-6">
        <div className="max-w-5xl mx-auto grid md:grid-cols-2 gap-10 items-center">
          <div>
            <h2 className="text-4xl font-bold mb-6">Our Mission</h2>
            <p className="text-muted-foreground text-lg leading-8">
              We aim to simplify authentication for modern applications.
              Security should not be complicated. Authify provides robust login,
              registration, OAuth2, token-based access, and scalable identity
              systems for developers and businesses.
            </p>
          </div>

          <Card className="rounded-2xl border-border bg-card/70 backdrop-blur-xl shadow-xl">
            <CardContent className="p-8 space-y-5">
              <div className="flex gap-4 items-center">
                <Shield className="w-8 h-8 text-primary" />
                <span className="text-lg font-medium">Enterprise Security</span>
              </div>

              <div className="flex gap-4 items-center">
                <Lock className="w-8 h-8 text-primary" />
                <span className="text-lg font-medium">Modern Token Auth</span>
              </div>

              <div className="flex gap-4 items-center">
                <Globe className="w-8 h-8 text-primary" />
                <span className="text-lg font-medium">Global Scalability</span>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Values Section */}
      <section className="py-24 px-6">
        <h2 className="text-4xl font-bold text-center mb-14">
          What We Stand For
        </h2>

        <div className="max-w-6xl mx-auto grid md:grid-cols-3 gap-8">
          {[
            {
              title: "Security First",
              desc: "Every feature is designed with strong security principles and best practices.",
              icon: <Shield className="w-10 h-10" />,
            },
            {
              title: "Developer Focused",
              desc: "Clear APIs, fast integration, and workflows that save development time.",
              icon: <Rocket className="w-10 h-10" />,
            },
            {
              title: "Trusted Reliability",
              desc: "Stable authentication systems built for startups and enterprises alike.",
              icon: <Users className="w-10 h-10" />,
            },
          ].map((item, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.15 }}
              viewport={{ once: true }}
            >
              <Card className="rounded-2xl border-border bg-card/70 backdrop-blur-xl">
                <CardContent className="p-8 text-center">
                  <div className="flex justify-center mb-5 text-primary">
                    {item.icon}
                  </div>
                  <h3 className="text-2xl font-semibold mb-3">{item.title}</h3>
                  <p className="text-muted-foreground">{item.desc}</p>
                </CardContent>
              </Card>
            </motion.div>
          ))}
        </div>
      </section>

      {/* Why Choose Section */}
      <section className="py-24 px-6">
        <h2 className="text-4xl font-bold text-center mb-12">
          Why Developers Choose Authify
        </h2>

        <div className="max-w-4xl mx-auto space-y-6">
          {[
            "JWT based secure authentication",
            "OAuth2 login with providers",
            "Clean frontend and backend integration",
            "Scalable architecture for production apps",
            "Modern UI and seamless user experience",
          ].map((point, i) => (
            <div
              key={i}
              className="flex items-center gap-4 p-4 rounded-xl border border-border bg-card/60"
            >
              <CheckCircle2 className="w-6 h-6 text-primary" />
              <span className="text-muted-foreground text-lg">{point}</span>
            </div>
          ))}
        </div>
      </section>

      {/* Footer */}
      <footer className="py-10 text-center text-muted-foreground border-t border-border">
        © {new Date().getFullYear()} Authify. All rights reserved.
      </footer>
    </div>
  );
}
