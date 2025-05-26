import tailwindcss from "@tailwindcss/vite";
import react from "@vitejs/plugin-react";
import { defineConfig, loadEnv } from "vite";
import flowbiteReact from "flowbite-react/plugin/vite";
import path from "path"

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const port = parseInt(env.VITE_PORT || '5173');
  const allowedHosts = (env.VITE_HOSTS || 'localhost')
    .split(',')
    .map(host => host.trim());
  
  return {
    plugins: [react(), tailwindcss(), flowbiteReact()],
    resolve: {
      alias: {
        "@": path.resolve(__dirname, "./src"),
      },
    },
    define: {
      'global': 'window',
    },
    server: {
      host: true,
      port,
      allowedHosts,
    },    
  };
});
