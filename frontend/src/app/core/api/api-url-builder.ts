import { environment } from "../../../environments/environment";

export function apiUrl(path: string): string {
  return `${environment.apiBase}${path}`;
}