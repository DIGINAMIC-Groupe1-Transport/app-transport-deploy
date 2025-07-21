export interface AuthResponse {
  status: string;
  data: {
    headers: Record<string, any>;
    body: string;  // c’est ton token JWT ici
    statusCodeValue: number;
    statusCode: string;
  };
  message: string;
  errors: any | null;
}