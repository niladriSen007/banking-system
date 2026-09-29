import { MutationCache, QueryCache, QueryClient } from "@tanstack/react-query";
import { toast } from "@/components/ui/toast";

const notifyRequestError = (error: Error) => {
	toast.add({
		type: "error",
		description: error.message,
		priority: "high",
	});
};

export const queryClient = new QueryClient({
	queryCache: new QueryCache({ onError: notifyRequestError }),
	mutationCache: new MutationCache({ onError: notifyRequestError }),
});
