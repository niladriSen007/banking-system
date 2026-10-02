import { create } from "zustand";


type AuthStore = {
  isPayAccountSetupDone: boolean;

  setIsPayAccountSetupDone: (setupDone: boolean) => void;
};

export const usePayStore = create<AuthStore>((set) => ({
  isPayAccountSetupDone: false,

  setIsPayAccountSetupDone: (setupDone: boolean) =>
    set({
      isPayAccountSetupDone: setupDone,
    }),
}));
