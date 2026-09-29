export const loginFormData: {
  key: string;
  name: "email" | "password";
  label: string;
}[] = [
  {
    key: "email",
    name: "email",
    label: "Email",
  },
  {
    key: "password",
    name: "password",
    label: "Password",
  },
];

export const signUpFormData: {
  key: string;
  name: "email" | "password" | "firstName" | "lastName" | "phoneNumber";
  label: string;
}[] = [
  {
    key: "firstName",
    name: "firstName",
    label: "FirstName",
  },
  {
    key: "lastName",
    name: "lastName",
    label: "LastName",
  },
  {
    key: "email",
    name: "email",
    label: "Email",
  },
  {
    key: "phoneNumber",
    name: "phoneNumber",
    label: "Phone number",
  },
  {
    key: "password",
    name: "password",
    label: "Password",
  },
];
