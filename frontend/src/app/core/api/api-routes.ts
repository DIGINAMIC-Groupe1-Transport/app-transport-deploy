export const ApiRoutes = {
  carpool: {
    organizedCarpools: '/user/carpools/organize',
    getParticipateCarpools: '/user/carpools/participate',
    getSearchedCarpools: '/user/carpools/search',
    deleteOrganizedCarpool: '/user/carpools/organize/{id}',
    participateCarpool: '/user/carpools/participate/{id}',
    deleteParticipateCarpool: '/user/carpools/participate/{id}',
    getCarpoolDetails: '/user/carpools/search/{id}',
  },
  authentication: '/auth/login',
  vehicle: {
    getPersonalVehicles: '/vehicles/personal',
    createPersonalVehicle: '/vehicles/personal/create',
    deletePersonalVehicle: '/vehicles/personal/delete/{id}',
    status: '/vehicles/status',
  },

  vehicleReservation: {
    getReservation: '/reservations',
    getReservationDetails: '/reservations/details',
    getReservationByUser: '/reservations/user',
    getReservationByVehicle: '/reservations/vehicle',
    createReservation: '/reservations',
    deleteReservation: (id: number) => `/reservations/${id}`,
    updateReservation: (reservation: any) => `/reservations/${reservation.id}`,
  },
  vehicleService: {
    getVehicleById: (id: number) => `/vehicles/service/${id}`,
    getVehicleByRegistration: '/vehicles/registration',
    getAllVehicles: '/vehicles/service/all',
    add: '/vehicles/service',
    update: (id: number) => `/vehicles/service/update/${id}`,
    delete: (id: number) => `/vehicles/service/delete/${id}`,
  },
};
