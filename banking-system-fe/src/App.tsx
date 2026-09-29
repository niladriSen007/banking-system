import { Toaster } from "./components/ui/toast";
import { QueryClientProvider } from "./provider/tanstack-query-client-provider";

const App = () => {
  return (
    <QueryClientProvider>
      <Toaster />
    </QueryClientProvider>
  );
};

export default App;
